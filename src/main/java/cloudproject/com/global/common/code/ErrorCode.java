package cloudproject.com.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "서버 내부 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C400", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "C002", "요청 본문의 JSON 형식이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C405", "지원하지 않는 HTTP 메서드입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C403", "접근 권한이 없습니다."),

    // Assignment
    ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "AS001", "과제를 찾을 수 없습니다."),
    ASSIGNMENT_ALREADY_PUBLISHED(HttpStatus.BAD_REQUEST, "AS002", "이미 게시된 과제입니다."),
    ASSIGNMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "AS003", "해당 과제에 대한 권한이 없습니다."),

    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Q001", "문항을 찾을 수 없습니다."),
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "제출 정보를 찾을 수 없습니다."),
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "파일 업로드 URL 생성에 실패했습니다."),

    CLASSROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CL001", "클래스를 찾을 수 없습니다."),
    CLASSROOM_FORBIDDEN(HttpStatus.FORBIDDEN, "CL002", "해당 반에 대한 권한이 없습니다."),
    STUDENT_ALREADY_ENROLLED(HttpStatus.CONFLICT, "CL003", "이미 해당 반에 등록된 학생입니다."),
    NOT_A_STUDENT(HttpStatus.BAD_REQUEST, "CL004", "학생 계정이 아닙니다."),
    // Auth
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "A001", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A002", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A003", "비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "유효하지 않은 토큰입니다."),
    // WrongNote
    WRONG_NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "WN001", "오답노트를 찾을 수 없습니다."),
    // Chat
    BEDROCK_INVOKE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CH001", "AI 튜터 호출에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
