package com.manruhomerun.yadan.notification.listener;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.manruhomerun.yadan.notification.event.NotificationCreatedEvent;
import com.manruhomerun.yadan.notification.service.NotificationPushService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
public class NotificationPushListener {

    private final NotificationPushService notificationPushService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        try {
            notificationPushService.sendNotification(event.notificationId());
        } catch (Exception exception) {
            log.error(
                    "알림 커밋 이후 푸시 처리에 실패했습니다. notificationId={}",
                    event.notificationId(),
                    exception
            );
        }
    }
}
