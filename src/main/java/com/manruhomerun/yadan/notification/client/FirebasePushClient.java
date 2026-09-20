package com.manruhomerun.yadan.notification.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
public class FirebasePushClient {

    private static final String NOTIFICATION_CHANNEL_ID = "yadan_notification";

    private final FirebaseMessaging firebaseMessaging;

    public String send(String fid, PushMessage pushMessage) throws FirebaseMessagingException {
        Message.Builder messageBuilder = Message.builder()
                .setFid(fid)
                .setNotification(
                        com.google.firebase.messaging.Notification.builder()
                                .setTitle(pushMessage.title())
                                .setBody(pushMessage.body())
                                .build()
                )
                .setAndroidConfig(createAndroidConfig())
                .putData("notificationId", pushMessage.notificationId().toString())
                .putData("type", pushMessage.type().name());

        if (pushMessage.referenceId() != null) {
            messageBuilder.putData("referenceId", pushMessage.referenceId());
        }

        return firebaseMessaging.send(messageBuilder.build());
    }

    public String sendTest(
            String fid,
            String title,
            String body,
            Long notificationId,
            NotificationType type,
            String referenceId,
            boolean dryRun
    ) throws FirebaseMessagingException {
        Message.Builder messageBuilder = Message.builder()
                .setFid(fid)
                .setNotification(
                        com.google.firebase.messaging.Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .setAndroidConfig(createAndroidConfig())
                .putData("notificationId", notificationId.toString())
                .putData("type", type.name());

        if (referenceId != null) {
            messageBuilder.putData("referenceId", referenceId);
        }

        return firebaseMessaging.send(messageBuilder.build(), dryRun);
    }

    private AndroidConfig createAndroidConfig() {
        return AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(
                        AndroidNotification.builder()
                                .setChannelId(NOTIFICATION_CHANNEL_ID)
                                .build()
                )
                .build();
    }
}
