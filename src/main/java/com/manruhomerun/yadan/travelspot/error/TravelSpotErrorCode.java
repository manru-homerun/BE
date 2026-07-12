package com.manruhomerun.yadan.travelspot.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum TravelSpotErrorCode implements BaseErrorCode {
    TRAVEL_SPOT_NOT_FOUND("TRAVEL_SPOT_404", "여행지를 찾을 수 없습니다.", 404);

    private final String code;
    private final String defaultMessage;
    private final int status;

    TravelSpotErrorCode(String code, String defaultMessage, int status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
