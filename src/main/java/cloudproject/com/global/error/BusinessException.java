package cloudproject.com.global.error;

import cloudproject.com.global.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        //부모클래스에 메세지 전달
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
