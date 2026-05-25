package cloudproject.com.grade.service;

import cloudproject.com.assignment.domain.Question;
import cloudproject.com.assignment.repository.QuestionRepository;
import cloudproject.com.auth.domain.Role;
import cloudproject.com.grade.domain.QuestionResult;
import cloudproject.com.grade.domain.Result;
import cloudproject.com.grade.domain.Submission;
import cloudproject.com.grade.dto.SubmissionResultResponse;
import cloudproject.com.grade.dto.SubmissionStatusResponse;
import cloudproject.com.grade.repository.QuestionResultRepository;
import cloudproject.com.grade.repository.SubmissionRepository;
import cloudproject.com.grade.support.ScoreCalculator;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_ACCESS_DENIED;
import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SubmissionResultService {

    private final SubmissionRepository submissionRepository;
    private final QuestionResultRepository questionResultRepository;
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public SubmissionResultResponse getSubmissionResult(Long submissionId, Long currentUserId, Role currentRole) {
        Submission submission = submissionRepository.findDetailById(submissionId)
                .orElseThrow(() -> new BusinessException(SUBMISSION_NOT_FOUND));

        validateAccess(submission, currentUserId, currentRole);

        return buildResultResponse(submission);
    }

    @Transactional(readOnly = true)
    public SubmissionResultResponse getStudentSubmissionResult(
            Long assignmentId, Long studentId, Long currentUserId, Role currentRole
    ) {
        if (currentRole != Role.TEACHER) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }

        Submission submission = submissionRepository
                .findLatestByAssignmentIdAndStudentId(assignmentId, studentId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(SUBMISSION_NOT_FOUND));

        validateAccess(submission, currentUserId, currentRole);

        return buildResultResponse(submission);
    }

    private SubmissionResultResponse buildResultResponse(Submission submission) {
        List<QuestionResult> questionResults =
                questionResultRepository.findAllBySubmissionIdWithQuestion(submission.getSubmissionId());

        Long maxScoreSum = questionRepository.sumMaxScoreByAssignmentId(
                submission.getAssignment().getAssignmentId()
        );
        int maxScore = maxScoreSum == null ? 0 : Math.toIntExact(maxScoreSum);

        int correct = countByResult(questionResults, Result.CORRECT);
        int partial = countByResult(questionResults, Result.PARTIAL);
        int wrong = countByResult(questionResults, Result.WRONG);

        List<SubmissionResultResponse.QuestionResultDto> questions = questionResults.stream()
                .map(this::toQuestionResultDto)
                .toList();

        return new SubmissionResultResponse(
                submission.getSubmissionId(),
                submission.getAssignment().getAssignmentId(),
                submission.getAssignment().getTitle(),
                submission.getStudent().getUserId(),
                submission.getStudent().getName(),
                submission.getTotalScore(),
                maxScore,
                ScoreCalculator.calculateCorrectRate(submission.getTotalScore(), maxScore),
                submission.getSubmittedAt(),
                submission.getGradedAt(),
                new SubmissionResultResponse.Summary(correct, partial, wrong),
                questions
        );
    }

    @Transactional(readOnly = true)
    public SubmissionStatusResponse getSubmissionStatus(Long submissionId, Long currentUserId, Role currentRole) {
        Submission submission = submissionRepository.findDetailById(submissionId)
                .orElseThrow(() -> new BusinessException(SUBMISSION_NOT_FOUND));

        validateAccess(submission, currentUserId, currentRole);

        return new SubmissionStatusResponse(
                submission.getSubmissionId(),
                submission.getGradingStatus(),
                submission.getSubmittedAt(),
                submission.getGradedAt(),
                submission.getFailReason()
        );
    }

    private void validateAccess(Submission submission, Long currentUserId, Role currentRole) {
        if (currentRole == Role.STUDENT && !submission.getStudent().getUserId().equals(currentUserId)) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }

        if (currentRole == Role.TEACHER && !submission.getAssignment().getTeacher().getUserId().equals(currentUserId)) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }

        if (currentRole != Role.STUDENT && currentRole != Role.TEACHER) {
            throw new BusinessException(SUBMISSION_ACCESS_DENIED);
        }
    }

    private int countByResult(List<QuestionResult> questionResults, Result result) {
        return (int) questionResults.stream()
                .filter(questionResult -> result == questionResult.getResult())
                .count();
    }

    private SubmissionResultResponse.QuestionResultDto toQuestionResultDto(QuestionResult questionResult) {
        Question question = questionResult.getQuestion();

        return new SubmissionResultResponse.QuestionResultDto(
                question.getQuestionId(),
                question.getContent(),
                questionResult.getResult(),
                questionResult.getScore(),
                question.getMaxScore(),
                questionResult.getImageUrl(),
                questionResult.getReason(),
                questionResult.getRegradeStatus()
        );
    }
}
