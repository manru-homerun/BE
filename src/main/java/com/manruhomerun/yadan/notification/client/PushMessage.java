package com.manruhomerun.yadan.notification.client;

import com.manruhomerun.yadan.notification.domain.entity.Notification;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;

public record PushMessage(
        Long notificationId,
        NotificationType type,
        String title,
        String body,
        String referenceId
) {

    public static PushMessage from(Notification notification) {
        return new PushMessage(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getReferenceId()
        );
    }
}
