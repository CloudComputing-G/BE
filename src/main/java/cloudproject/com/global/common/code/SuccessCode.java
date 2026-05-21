package cloudproject.com.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "S001", "Health Check Success"),

    // Auth
    SIGNUP_SUCCESS(HttpStatus.CREATED, "S002", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "S003", "로그인이 완료되었습니다."),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "S004", "토큰이 재발급되었습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
