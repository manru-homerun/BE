package com.manruhomerun.yadan.global.error;

public class CustomException extends RuntimeException {

    private final BaseErrorCode errorCode;

    protected CustomException(BaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}
