package cloudproject.com.grade.service;

import cloudproject.com.assignment.domain.Question;
import cloudproject.com.assignment.repository.QuestionRepository;
import cloudproject.com.grade.domain.AnalyticsRecord;
import cloudproject.com.grade.domain.QuestionResult;
import cloudproject.com.grade.domain.Submission;
import cloudproject.com.grade.dto.GradingResultRequest;
import cloudproject.com.grade.repository.AnalyticsRecordRepository;
import cloudproject.com.grade.repository.QuestionResultRepository;
import cloudproject.com.grade.repository.SubmissionRepository;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final AnalyticsRecordRepository analyticsRecordRepository;
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
        Submission submission = submissionRepository.findDetailById(submissionId)
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

        List<Long> questionIds = request.questions().stream()
                .map(GradingResultRequest.QuestionResultItem::questionId)
                .toList();
        Map<Long, Question> questionMap = questionRepository.findAllById(questionIds).stream()
                .collect(Collectors.toMap(Question::getQuestionId, Function.identity()));

        Map<String, int[]> typeStats = new LinkedHashMap<>();
        List<QuestionResult> results = new java.util.ArrayList<>();
        for (GradingResultRequest.QuestionResultItem item : request.questions()) {
            Question question = questionMap.get(item.questionId());
            if (question == null) throw new BusinessException(QUESTION_NOT_FOUND);

            question.updateCategory(item.category());
            question.updateDetectedType(item.detectedType());

            int score = item.score() == null ? 0 : item.score();
            int maxScore = question.getMaxScore() == null ? 0 : question.getMaxScore();
            String result = computeResult(score, maxScore);

            String qType = (item.questionType() != null && !item.questionType().isBlank())
                    ? item.questionType() : "GENERAL";
            question.updateQuestionType(qType);
            typeStats.computeIfAbsent(qType, k -> new int[]{0, 0});
            typeStats.get(qType)[1]++;
            if (!RESULT_CORRECT.equals(result)) {
                typeStats.get(qType)[0]++;
            }

            results.add(QuestionResult.of(
                    submission, question, score, result, item.reason(), null,
                    item.needsManualReview()
            ));
        }
        questionResultRepository.saveAll(results);

        int totalScore = request.totalScore() == null ? 0 : request.totalScore();
        submission.complete(totalScore, gradedAt);

        saveAnalytics(submission, typeStats);
    }

    private void saveAnalytics(Submission submission, Map<String, int[]> typeStats) {
        Long studentId = submission.getStudent().getUserId();
        Long assignmentId = submission.getAssignment().getAssignmentId();

        analyticsRecordRepository.deleteByStudentIdAndAssignmentId(studentId, assignmentId);

        typeStats.forEach((questionType, counts) -> {
            AnalyticsRecord record = AnalyticsRecord.create(
                    submission.getStudent(),
                    submission.getAssignment(),
                    questionType,
                    counts[0],
                    counts[1]
            );
            analyticsRecordRepository.save(record);
        });
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
