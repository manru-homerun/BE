package com.manruhomerun.yadan.global.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.global.error.CommonErrorCode;

public class ExternalApiCallException extends BaseException {

    private final String responseBody;

    public ExternalApiCallException(String message) {
        this(message, null);
    }

    public ExternalApiCallException(String message, String responseBody) {
        super(CommonErrorCode.EXTERNAL_API_CALL_FAILED, message);
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
