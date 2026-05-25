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

    // Assignment
    ASSIGNMENT_CREATED(HttpStatus.CREATED, "S010", "과제가 생성되었습니다."),
    ASSIGNMENT_UPDATED(HttpStatus.OK, "S011", "과제가 수정되었습니다."),
    ASSIGNMENT_DELETED(HttpStatus.OK, "S012", "과제가 삭제되었습니다."),
    ASSIGNMENT_PUBLISHED(HttpStatus.OK, "S013", "과제가 게시되었습니다."),
    PRESIGNED_URL_GENERATED(HttpStatus.OK, "S014", "업로드 URL이 발급되었습니다."),
    UPLOAD_CONFIRMED(HttpStatus.OK, "S015", "업로드가 확인되었습니다."),
    SUBMISSION_CREATED(HttpStatus.CREATED, "S016", "제출 URL이 발급되었습니다."),
    SUBMISSION_CONFIRMED(HttpStatus.OK, "S017", "제출이 완료되었습니다."),
    QUESTION_UPDATED(HttpStatus.OK, "S018", "문항이 수정되었습니다."),
    ASSIGNMENT_FETCHED(HttpStatus.OK, "S019", "과제 조회가 완료되었습니다."),
    ASSIGNMENTS_FETCHED(HttpStatus.OK, "S020", "과제 목록 조회가 완료되었습니다."),

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
