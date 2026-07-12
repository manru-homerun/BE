package com.manruhomerun.yadan.baseball.error;

import com.manruhomerun.yadan.global.error.BaseException;

public class BaseballResourceNotFoundException extends BaseException {

    public BaseballResourceNotFoundException(BaseballErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
