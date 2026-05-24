package cloudproject.com.global.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3uploadResult {
    private String presignedUrl;
    private String s3Key;
}
