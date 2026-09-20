package com.manruhomerun.yadan.notification.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum NotificationErrorCode implements BaseErrorCode {
    SETTING_NOT_FOUND("NOTIFICATION_404_SETTING", "알림 설정을 찾을 수 없습니다.", 404);

    private final String code;
    private final String defaultMessage;
    private final int status;

    NotificationErrorCode(String code, String defaultMessage, int status) {
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
