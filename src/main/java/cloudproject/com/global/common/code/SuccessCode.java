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
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "S004", "토큰이 재발급되었습니다."),

    // Grade
    SUBMISSION_RESULT_FETCH_SUCCESS(HttpStatus.OK, "S101", "제출물 채점 결과를 조회했습니다."),
    SUBMISSION_STATUS_FETCH_SUCCESS(HttpStatus.OK, "S102", "채점 상태를 조회했습니다."),
    REGRADE_REQUEST_SUCCESS(HttpStatus.OK, "S103", "재채점이 요청되었습니다."),
    REGRADE_CONFIRM_SUCCESS(HttpStatus.OK, "S104", "재채점 결과가 확정되었습니다."),
    REGRADE_REQUEST_LIST_FETCH_SUCCESS(HttpStatus.OK, "S105", "재채점 요청 목록을 조회했습니다."),
    LEADERBOARD_FETCH_SUCCESS(HttpStatus.OK, "S106", "과제 리더보드를 조회했습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
