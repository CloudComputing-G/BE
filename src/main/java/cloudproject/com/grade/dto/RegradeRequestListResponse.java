package cloudproject.com.grade.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RegradeRequestListResponse(
        Long assignmentId,
        List<Item> regradeRequests
) {
    public record Item(
            Long submissionId,
            Long questionId,
            Long studentId,
            String studentName,
            String questionContent,
            Integer currentScore,
            Integer maxScore,
            String currentResult,
            String reason,
            String imageUrl,
            LocalDateTime submittedAt
    ) {
    }
}
