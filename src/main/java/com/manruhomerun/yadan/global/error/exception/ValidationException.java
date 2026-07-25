package com.manruhomerun.yadan.global.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.global.error.CommonErrorCode;

public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(CommonErrorCode.VALIDATION_FAILED, message);
    }
}
