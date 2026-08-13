package com.manruhomerun.yadan.travel.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;

public class TravelNotFoundException extends BaseException {
    public TravelNotFoundException(TravelErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
