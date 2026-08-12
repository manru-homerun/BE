package com.manruhomerun.yadan.auth.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum AuthErrorCode implements BaseErrorCode {
    UNSUPPORTED_PROVIDER("AUTH_400_UNSUPPORTED_PROVIDER", "지원하지 않는 소셜 로그인 제공자입니다.", 400),
    WITHDRAWN_USER("AUTH_403_WITHDRAWN_USER", "탈퇴한 회원은 로그인할 수 없습니다.", 403),
    INVALID_ACCESS_TOKEN("AUTH_401_INVALID_ACCESS_TOKEN", "유효하지 않은 액세스 토큰입니다.", 401),
    INVALID_REFRESH_TOKEN("AUTH_401_INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다.", 401),
    INVALID_KAKAO_TOKEN("AUTH_401_INVALID_KAKAO_TOKEN", "유효하지 않은 카카오 액세스 토큰입니다.", 401),
    KAKAO_API_CALL_FAILED("AUTH_502_KAKAO_API_CALL_FAILED", "카카오 API 호출에 실패했습니다.", 502);

    private final String code;
    private final String defaultMessage;
    private final int status;

    AuthErrorCode(String code, String defaultMessage, int status) {
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
