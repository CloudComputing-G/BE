package cloudproject.com.global.s3;

import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3uploadResult getTeacherProblemUploadUrl(String fileExtension) {
        String s3Key = String.format("assignments/problem/%s.%s", UUID.randomUUID(),fileExtension);
        String presignedUrl = generatePresignedUrl(s3Key,fileExtension);
        return new S3uploadResult(presignedUrl, s3Key);
    }

    public S3uploadResult getTeacherAnswerUploadUrl(String fileExtension) {
        String s3Key = String.format("assignments/answer/%s.%s",
                UUID.randomUUID(), fileExtension);
        String presignedUrl = generatePresignedUrl(s3Key, fileExtension);
        return new S3uploadResult(presignedUrl, s3Key);
    }

    public S3uploadResult getStudentUploadUrl(Long assignmentId, Long studentId, String fileExtension) {
        String s3Key = String.format("submissions/%d/%d/%s.%s",
                assignmentId, studentId, UUID.randomUUID(), fileExtension);
        String presignedUrl = generatePresignedUrl(s3Key, fileExtension);
        return new S3uploadResult(presignedUrl, s3Key);
    }

    private String generatePresignedUrl(String s3Key, String fileExtension) {
        try {
            String contentType = switch (fileExtension.toLowerCase()) {
                case "pdf"  -> "application/pdf";
                case "png"  -> "image/png";
                case "heic" -> "image/heic";
                default     -> "image/jpeg";
            };

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(putObjectRequest)
                    .build();

            return s3Presigner.presignPutObject(presignRequest).url().toString();

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL);
        }
    }

    public String getDownloadUrl(String s3Key){
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(120))
                    .getObjectRequest(getObjectRequest)
                    .build();
            return s3Presigner.presignGetObject(presignRequest).url().toString();
        }
        catch (Exception e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL);
        }
    }
}
