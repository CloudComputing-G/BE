package cloudproject.com.grade.service;

import cloudproject.com.assignment.domain.Question;
import cloudproject.com.assignment.repository.QuestionRepository;
import cloudproject.com.grade.domain.QuestionResult;
import cloudproject.com.grade.domain.Submission;
import cloudproject.com.grade.dto.GradingResultRequest;
import cloudproject.com.grade.repository.QuestionResultRepository;
import cloudproject.com.grade.repository.SubmissionRepository;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cloudproject.com.global.common.code.ErrorCode.GRADING_ALREADY_COMPLETED;
import static cloudproject.com.global.common.code.ErrorCode.INVALID_INPUT_VALUE;
import static cloudproject.com.global.common.code.ErrorCode.QUESTION_NOT_FOUND;
import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_NOT_FOUND;
import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_NOT_GRADED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradingService {

    private static final String GRADING_STATUS_PENDING = "PENDING";
    private static final String REPORT_STATUS_DONE = "DONE";
    private static final String REPORT_STATUS_FAILED = "FAILED";
    private static final String RESULT_CORRECT = "CORRECT";
    private static final String RESULT_PARTIAL = "PARTIAL";
    private static final String RESULT_WRONG = "WRONG";

    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionResultRepository questionResultRepository;
    private final GradingJobPublisher gradingJobPublisher;

    @Transactional
    public void startGrading(Long submissionId) {
        Submission submission = submissionRepository.findDetailById(submissionId)
                .orElseThrow(() -> new BusinessException(SUBMISSION_NOT_FOUND));

        if (!GRADING_STATUS_PENDING.equals(submission.getGradingStatus())) {
            throw new BusinessException(SUBMISSION_NOT_GRADED);
        }

        gradingJobPublisher.publish(submission);
        log.info("Grading triggered for submissionId={}", submissionId);
    }

    @Transactional
    public void reportResult(Long submissionId, GradingResultRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException(SUBMISSION_NOT_FOUND));

        if (!GRADING_STATUS_PENDING.equals(submission.getGradingStatus())) {
            throw new BusinessException(GRADING_ALREADY_COMPLETED);
        }

        LocalDateTime gradedAt = request.gradedAt() != null ? request.gradedAt() : LocalDateTime.now();

        if (REPORT_STATUS_FAILED.equals(request.status())) {
            submission.fail(request.failReason(), gradedAt);
            return;
        }

        if (!REPORT_STATUS_DONE.equals(request.status())) {
            throw new BusinessException(INVALID_INPUT_VALUE);
        }

        if (request.questions() == null || request.questions().isEmpty()) {
            throw new BusinessException(INVALID_INPUT_VALUE);
        }

        questionResultRepository.deleteAllBySubmissionId(submissionId);

        for (GradingResultRequest.QuestionResultItem item : request.questions()) {
            Question question = questionRepository.findById(item.questionId())
                    .orElseThrow(() -> new BusinessException(QUESTION_NOT_FOUND));

            question.updateCategory(item.category());
            question.updateDetectedType(item.detectedType());

            int score = item.score() == null ? 0 : item.score();
            int maxScore = question.getMaxScore() == null ? 0 : question.getMaxScore();
            String result = computeResult(score, maxScore);

            QuestionResult qr = QuestionResult.of(
                    submission, question, score, result, item.reason(), null,
                    item.needsManualReview()
            );
            questionResultRepository.save(qr);
        }

        int totalScore = request.totalScore() == null ? 0 : request.totalScore();
        submission.complete(totalScore, gradedAt);
    }

    private String computeResult(int score, int maxScore) {
        if (maxScore > 0 && score == maxScore) {
            return RESULT_CORRECT;
        }
        if (score == 0) {
            return RESULT_WRONG;
        }
        return RESULT_PARTIAL;
    }
}
