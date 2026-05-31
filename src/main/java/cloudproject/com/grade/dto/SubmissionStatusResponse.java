package cloudproject.com.grade.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubmissionStatusResponse(
        Long submissionId,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime gradedAt,
        String failReason
) {
}
