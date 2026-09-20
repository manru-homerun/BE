package com.manruhomerun.yadan.notification.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.notification.error.NotificationErrorCode;

public class PushInstallationNotFoundException extends BaseException {

    public PushInstallationNotFoundException() {
        super(
                NotificationErrorCode.PUSH_INSTALLATION_NOT_FOUND,
                NotificationErrorCode.PUSH_INSTALLATION_NOT_FOUND.getDefaultMessage()
        );
    }
}
