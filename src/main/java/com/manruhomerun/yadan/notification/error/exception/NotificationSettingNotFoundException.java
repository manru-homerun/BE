package com.manruhomerun.yadan.notification.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.notification.error.NotificationErrorCode;

public class NotificationSettingNotFoundException extends BaseException {

    public NotificationSettingNotFoundException() {
        super(
                NotificationErrorCode.SETTING_NOT_FOUND,
                NotificationErrorCode.SETTING_NOT_FOUND.getDefaultMessage()
        );
    }
}
