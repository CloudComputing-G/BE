package cloudproject.com.grade.dto;

import cloudproject.com.grade.domain.GradingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubmissionStatusResponse(
        Long submissionId,
        GradingStatus status,
        LocalDateTime requestedAt,
        LocalDateTime gradedAt,
        String failReason
) {
}
