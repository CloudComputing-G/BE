package cloudproject.com.application.image.dto.response;

import lombok.Getter;

@Getter
public class PresignedUrlResponse {

    private Long submissionId;
    private String presignedUrl; // S3에 직접 업로드할 URL
    private String s3Key;        // 업로드 후 confirm 때 사용할 경로

    public static PresignedUrlResponse of(Long submissionId, String presignedUrl, String s3Key) {
        PresignedUrlResponse dto = new PresignedUrlResponse();
        dto.submissionId = submissionId;
        dto.presignedUrl = presignedUrl;
        dto.s3Key = s3Key;
        return dto;
    }
}
