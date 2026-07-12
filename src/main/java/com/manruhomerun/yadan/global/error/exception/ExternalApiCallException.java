package com.manruhomerun.yadan.global.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.global.error.CommonErrorCode;

public class ExternalApiCallException extends BaseException {

    public ExternalApiCallException(String message) {
        super(CommonErrorCode.EXTERNAL_API_CALL_FAILED, message);
    }
}
