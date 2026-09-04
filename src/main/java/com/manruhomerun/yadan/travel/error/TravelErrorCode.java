package com.manruhomerun.yadan.travel.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum TravelErrorCode implements BaseErrorCode {
    TRAVEL_NOT_FOUND("TRAVEL_404", "여행을 찾을 수 없습니다.", 404);

    private final String code;
    private final String defaultMessage;
    private final int status;

    TravelErrorCode(String code, String defaultMessage, int status) {
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
