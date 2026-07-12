package com.manruhomerun.yadan.global.error;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException() {
        super(CommonErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }
}
