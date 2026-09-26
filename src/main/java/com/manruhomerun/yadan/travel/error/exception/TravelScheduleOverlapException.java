package com.manruhomerun.yadan.travel.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;

public class TravelScheduleOverlapException extends BaseException {
    public TravelScheduleOverlapException() {
        super(TravelErrorCode.TRAVEL_SCHEDULE_OVERLAP,
                TravelErrorCode.TRAVEL_SCHEDULE_OVERLAP.getDefaultMessage());
    }
}
