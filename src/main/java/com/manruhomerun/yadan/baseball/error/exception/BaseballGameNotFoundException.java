package com.manruhomerun.yadan.baseball.error.exception;

import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.global.error.BaseException;

public class BaseballGameNotFoundException extends BaseException {
    public BaseballGameNotFoundException(BaseballErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
