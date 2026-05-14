package cloudproject.com.global.common;

import cloudproject.com.global.common.code.BaseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        List<FieldError> fieldErrors
){
    public static <T> ApiResponse<T> success(BaseCode successCode, T data){
        return new ApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                data,
                null
        );
    }
    public static ApiResponse<Void> success(BaseCode successCode) {
        return new ApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                null,
                null
        );
    }

    public static ApiResponse<Void> failure(BaseCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                null
        );
    }

    // @Valud 검증 실패시
    public static ApiResponse<Void> failure(BaseCode errorCode, List<FieldError> fieldErrors) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                fieldErrors
        );
    }

    public record FieldError(
            String field,
            Object rejectedValue,
            String reason
    ) {
    }


}
