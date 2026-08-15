package com.manruhomerun.yadan.user.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum UserErrorCode implements BaseErrorCode {
    ONBOARDING_ALREADY_COMPLETED(
            "USER_ONBOARDING_409_ALREADY_COMPLETED",
            "이미 온보딩을 완료했습니다.",
            409
    ),
    NICKNAME_ALREADY_EXISTS(
            "USER_NICKNAME_409_ALREADY_EXISTS",
            "이미 사용 중인 닉네임입니다.",
            409
    ),
    REQUIRED_AGREEMENT_NOT_ACCEPTED(
            "USER_AGREEMENT_400_REQUIRED_NOT_ACCEPTED",
            "필수 약관에 동의해야 합니다.",
            400
    ),
    INVALID_TRAVEL_REGION(
            "USER_TRAVEL_REGION_400_INVALID",
            "지원하지 않는 여행 지역입니다.",
            400
    );

    private final String code;
    private final String defaultMessage;
    private final int status;

    UserErrorCode(String code, String defaultMessage, int status) {
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
