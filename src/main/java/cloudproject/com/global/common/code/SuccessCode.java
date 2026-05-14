package cloudproject.com.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "S001", "Health Check Success");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
