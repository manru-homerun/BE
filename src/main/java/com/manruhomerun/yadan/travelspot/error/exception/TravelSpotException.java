package com.manruhomerun.yadan.travelspot.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.travelspot.error.TravelSpotErrorCode;

public class TravelSpotException extends BaseException {

    public TravelSpotException(TravelSpotErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
