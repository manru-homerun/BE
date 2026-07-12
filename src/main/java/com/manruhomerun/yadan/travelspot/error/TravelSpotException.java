package com.manruhomerun.yadan.travelspot.error;

import com.manruhomerun.yadan.global.error.BaseException;

public class TravelSpotException extends BaseException {

    public TravelSpotException(TravelSpotErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
