package cloudproject.com.grade.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SubmissionResultResponse(
        Long submissionId,
        Long assignmentId,
        String assignmentTitle,
        Long studentId,
        String studentName,
        Integer totalScore,
        Integer maxScore,
        Double correctRate,
        LocalDateTime submittedAt,
        LocalDateTime gradedAt,
        Summary summary,
        List<QuestionResultDto> questions
) {
    public record Summary(
            int correct,
            int partial,
            int wrong
    ) {
    }

    public record QuestionResultDto(
            Long questionId,
            String questionContent,
            String result,
            Integer score,
            Integer maxScore,
            String imageUrl,
            String reason,
            String regradeStatus,
            String category,
            String detectedType,
            Boolean needsManualReview
    ) {
    }
}
