package com.manruhomerun.yadan.global.error;

public class ExternalApiCallException extends BaseException {

    public ExternalApiCallException(String message) {
        super(CommonErrorCode.EXTERNAL_API_CALL_FAILED, message);
    }
}
