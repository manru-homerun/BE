package com.manruhomerun.yadan.friend.error.exception;

import com.manruhomerun.yadan.friend.error.FriendErrorCode;
import com.manruhomerun.yadan.global.error.BaseException;

public class FriendException extends BaseException {

    public FriendException(FriendErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }
}
