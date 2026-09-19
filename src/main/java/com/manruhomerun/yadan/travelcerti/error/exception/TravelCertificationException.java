package com.manruhomerun.yadan.travelcerti.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.travelcerti.error.TravelCertificationErrorCode;

public class TravelCertificationException extends BaseException {

    public TravelCertificationException(TravelCertificationErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
