package cloudproject.com.global.error;

import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. 직접 던진 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(errorCode));
    }

    // 2. @Valid 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getBindingResult().getAllErrors());

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        BindingResult bindingResult = e.getBindingResult();

        List<ApiResponse.FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> new ApiResponse.FieldError(
                        fieldError.getField(),
                        fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(errorCode, fieldErrors));
    }

    // 3. JSON 형식 오류 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Invalid JSON: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_JSON;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(errorCode));
    }

    // 4. 잘못된 HTTP Method 처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not allowed: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(errorCode));
    }

    // 5. 파라미터 타입 불일치 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Type mismatch: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(errorCode));
    }

    // 6. 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("Unexpected Error", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(errorCode));
    }
}
