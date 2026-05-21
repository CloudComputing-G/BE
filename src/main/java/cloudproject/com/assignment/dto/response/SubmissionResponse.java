package cloudproject.com.assignment.dto.response;

import cloudproject.com.grade.domain.Submission;
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
