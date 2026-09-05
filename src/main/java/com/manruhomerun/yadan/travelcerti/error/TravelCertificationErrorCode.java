package com.manruhomerun.yadan.travelcerti.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum TravelCertificationErrorCode implements BaseErrorCode {
    TRAVEL_USER_NOT_FOUND("TRAVEL_CERTIFICATION_USER_404", "여행 참여 정보를 찾을 수 없습니다.", 404),
    TRAVEL_SPOT_NOT_FOUND("TRAVEL_CERTIFICATION_SPOT_404", "여행에 포함된 여행지를 찾을 수 없습니다.", 404),
    LOCATION_OUT_OF_RANGE("TRAVEL_CERTIFICATION_LOCATION_400", "여행지 방문 인증 가능 범위를 벗어났습니다.", 400);

    private final String code;
    private final String defaultMessage;
    private final int status;

    TravelCertificationErrorCode(String code, String defaultMessage, int status) {
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
