package com.manruhomerun.yadan.global.error;

public class BaseException extends RuntimeException {

    private final BaseErrorCode errorCode;

    protected BaseException(BaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}
