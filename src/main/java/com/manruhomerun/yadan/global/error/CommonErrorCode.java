package com.manruhomerun.yadan.global.error;

public enum CommonErrorCode implements BaseErrorCode {
    USER_NOT_FOUND("COMMON_USER_404", "사용자를 찾을 수 없습니다.", 404),
    EXTERNAL_API_CALL_FAILED("COMMON_EXTERNAL_API_502", "외부 API 호출에 실패했습니다.", 502);

    private final String code;
    private final String defaultMessage;
    private final int status;

    CommonErrorCode(String code, String defaultMessage, int status) {
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
