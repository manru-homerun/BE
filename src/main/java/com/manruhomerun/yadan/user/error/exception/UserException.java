package com.manruhomerun.yadan.user.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.user.error.UserErrorCode;

public class UserException extends BaseException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }

    public UserException(UserErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
