package com.manruhomerun.yadan.global.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.global.error.CommonErrorCode;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException() {
        super(CommonErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }
}
