package cloudproject.com.application.assignment.dto.response;

import cloudproject.com.application.domain.Submission;
import lombok.Getter;

@Getter
public class SubmissionResponse {
    private Long submissionId;
    private String gradingStatus; // "PENDING"

    public static SubmissionResponse from(Submission submission) {
        SubmissionResponse dto = new SubmissionResponse();
        dto.submissionId = submission.getSubmissionId();
        dto.gradingStatus = submission.getGradingStatus();
        return dto;
    }
}
