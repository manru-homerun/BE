package com.manruhomerun.yadan.baseball.error;

import com.manruhomerun.yadan.global.error.CustomException;

public class BaseballResourceNotFoundException extends CustomException {

    public BaseballResourceNotFoundException(BaseballErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
