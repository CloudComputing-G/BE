package cloudproject.com.grade.service;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.assignment.repository.AssignmentRepository;
import cloudproject.com.auth.domain.Role;
import cloudproject.com.grade.domain.GradingStatus;
import cloudproject.com.grade.domain.QuestionResult;
import cloudproject.com.grade.domain.RegradeStatus;
import cloudproject.com.grade.domain.Result;
import cloudproject.com.grade.dto.RegradeConfirmResponse;
import cloudproject.com.grade.dto.RegradeRequestListResponse;
import cloudproject.com.grade.dto.RegradeResponse;
import cloudproject.com.grade.repository.QuestionResultRepository;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cloudproject.com.global.common.code.ErrorCode.ASSIGNMENT_ACCESS_DENIED;
import static cloudproject.com.global.common.code.ErrorCode.ASSIGNMENT_NOT_FOUND;
import static cloudproject.com.global.common.code.ErrorCode.INVALID_SCORE;
import static cloudproject.com.global.common.code.ErrorCode.QUESTION_RESULT_NOT_FOUND;
import static cloudproject.com.global.common.code.ErrorCode.REGRADE_NOT_PENDING;
import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_ACCESS_DENIED;
import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_NOT_GRADED;

@Service
@RequiredArgsConstructor
public class RegradeService {

    private final QuestionResultRepository questionResultRepository;
    private final AssignmentRepository assignmentRepository;

    @Transactional
    public RegradeResponse requestRegrade(
            Long submissionId, Long questionId, Long currentUserId, Role currentRole
    ) {
        if (currentRole != Role.STUDENT) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }

        QuestionResult questionResult = questionResultRepository
                .findBySubmissionIdAndQuestionId(submissionId, questionId)
                .orElseThrow(() -> new BusinessException(QUESTION_RESULT_NOT_FOUND));

        if (!questionResult.getSubmission().getStudent().getUserId().equals(currentUserId)) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }

        if (questionResult.getSubmission().getGradingStatus() != GradingStatus.DONE) {
            throw new BusinessException(SUBMISSION_NOT_GRADED);
        }

        if (questionResult.getRegradeStatus() != RegradeStatus.PENDING) {
            questionResult.requestRegrade();
        }

        return new RegradeResponse(questionId, RegradeStatus.PENDING);
    }

    @Transactional
    public RegradeConfirmResponse confirmRegrade(
            Long submissionId, Long questionId, Integer score,
            Long currentUserId, Role currentRole
    ) {
        if (currentRole != Role.TEACHER) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }

        QuestionResult questionResult = questionResultRepository
                .findBySubmissionIdAndQuestionId(submissionId, questionId)
                .orElseThrow(() -> new BusinessException(QUESTION_RESULT_NOT_FOUND));

        if (!questionResult.getSubmission().getAssignment().getTeacher().getUserId().equals(currentUserId)) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }

        if (questionResult.getRegradeStatus() != RegradeStatus.PENDING) {
            throw new BusinessException(REGRADE_NOT_PENDING);
        }

        if (score == null) {
            questionResult.rejectRegrade();
        } else {
            int maxScore = questionResult.getQuestion().getMaxScore() == null
                    ? 0
                    : questionResult.getQuestion().getMaxScore();
            if (score < 0 || score > maxScore) {
                throw new BusinessException(INVALID_SCORE);
            }

            Result newResult = computeResult(score, maxScore);
            questionResult.confirmRegradeWithScore(score, newResult);

            recalculateTotalScore(submissionId, questionResult);
        }

        return new RegradeConfirmResponse(
                questionId,
                questionResult.getScore(),
                questionResult.getResult(),
                RegradeStatus.DONE,
                questionResult.getSubmission().getTotalScore()
        );
    }

    @Transactional(readOnly = true)
    public RegradeRequestListResponse getPendingRegradeRequests(
            Long assignmentId, Long currentUserId, Role currentRole
    ) {
        if (currentRole != Role.TEACHER) {
            throw new BusinessException(ASSIGNMENT_ACCESS_DENIED);
        }

        Assignment assignment = assignmentRepository.findByIdWithTeacher(assignmentId)
                .orElseThrow(() -> new BusinessException(ASSIGNMENT_NOT_FOUND));

        if (!assignment.getTeacher().getUserId().equals(currentUserId)) {
            throw new BusinessException(ASSIGNMENT_ACCESS_DENIED);
        }

        List<QuestionResult> pending = questionResultRepository
                .findByAssignmentIdAndRegradeStatus(assignmentId, RegradeStatus.PENDING);

        List<RegradeRequestListResponse.Item> items = pending.stream()
                .map(qr -> new RegradeRequestListResponse.Item(
                        qr.getSubmission().getSubmissionId(),
                        qr.getQuestion().getQuestionId(),
                        qr.getSubmission().getStudent().getUserId(),
                        qr.getSubmission().getStudent().getName(),
                        qr.getQuestion().getContent(),
                        qr.getScore(),
                        qr.getQuestion().getMaxScore(),
                        qr.getResult(),
                        qr.getReason(),
                        qr.getImageUrl(),
                        qr.getSubmission().getSubmittedAt()
                ))
                .toList();

        return new RegradeRequestListResponse(assignmentId, items);
    }

    private void recalculateTotalScore(Long submissionId, QuestionResult updated) {
        List<QuestionResult> all =
                questionResultRepository.findAllBySubmissionIdWithQuestion(submissionId);
        int newTotal = all.stream()
                .mapToInt(r -> r.getScore() == null ? 0 : r.getScore())
                .sum();
        updated.getSubmission().updateTotalScore(newTotal);
    }

    private Result computeResult(int score, int maxScore) {
        if (maxScore > 0 && score == maxScore) {
            return Result.CORRECT;
        }
        if (score == 0) {
            return Result.WRONG;
        }
        return Result.PARTIAL;
    }
}
