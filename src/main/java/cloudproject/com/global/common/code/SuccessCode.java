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
    // Classroom
    CLASSROOM_CREATED(HttpStatus.CREATED, "CL001", "반이 생성되었습니다."),
    CLASSROOM_FETCHED(HttpStatus.OK, "CL002", "반 조회가 완료되었습니다."),
    CLASSROOMS_FETCHED(HttpStatus.OK, "CL003", "반 목록 조회가 완료되었습니다."),
    CLASSROOM_UPDATED(HttpStatus.OK, "CL004", "반 정보가 수정되었습니다."),
    CLASSROOM_DELETED(HttpStatus.OK, "CL005", "반이 삭제되었습니다."),
    STUDENTS_ADDED(HttpStatus.CREATED, "CL006", "학생이 반에 추가되었습니다."),
    // WrongNote
    WRONG_NOTES_FETCHED(HttpStatus.OK, "WN001", "오답노트 목록 조회가 완료되었습니다."),
    WRONG_NOTE_FETCHED(HttpStatus.OK, "WN002", "오답노트 상세 조회가 완료되었습니다."),
    // Analytics
    WEAK_POINTS_FETCHED(HttpStatus.OK, "AN001", "취약점 분석 목록 조회가 완료되었습니다."),
    ASSIGNMENT_ANALYTICS_FETCHED(HttpStatus.OK, "AN002", "과제별 학생 통계 조회가 완료되었습니다."),
    // Chat
    CHAT_SUCCESS(HttpStatus.OK, "CH001", "AI 튜터 응답이 완료되었습니다."),
    CHAT_HISTORY_FETCHED(HttpStatus.OK, "CH002", "대화 기록 조회가 완료되었습니다."),
    SIMILAR_PROBLEM_GENERATED(HttpStatus.OK, "CH003", "유사문제 생성이 완료되었습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
