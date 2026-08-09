package com.manruhomerun.yadan.auth.error.exception;

import com.manruhomerun.yadan.auth.error.AuthErrorCode;
import com.manruhomerun.yadan.global.error.BaseException;

public class AuthException extends BaseException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }
}
