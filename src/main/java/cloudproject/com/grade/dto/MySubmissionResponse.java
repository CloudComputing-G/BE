package cloudproject.com.grade.dto;

import java.time.LocalDateTime;

public record MySubmissionResponse(
        Long submissionId,
        Long assignmentId,
        String assignmentTitle,
        String gradingStatus,
        Integer totalScore,
        LocalDateTime submittedAt
) {
}
