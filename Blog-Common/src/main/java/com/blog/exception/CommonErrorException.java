package com.blog.exception;


import com.blog.exception.interfaces.BaseErrorInterface;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yujunhong
 * @date 2021/4/29 10:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误信息
     */
    protected String errorMessage;

    public CommonErrorException() {
        super();
    }

    public CommonErrorException(BaseErrorInterface baseErrorInterface) {
        super(baseErrorInterface.getErrorCode());
        this.errorCode = baseErrorInterface.getErrorCode();
        this.errorMessage = baseErrorInterface.getErrorMessage();
    }

    public CommonErrorException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public CommonErrorException(String errorCode, String errorMessage) {
        super(errorCode);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public CommonErrorException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, throwable);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
