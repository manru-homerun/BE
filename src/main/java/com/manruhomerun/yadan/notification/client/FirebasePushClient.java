package com.manruhomerun.yadan.notification.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
public class FirebasePushClient {

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
                .putData("notificationId", pushMessage.notificationId().toString())
                .putData("type", pushMessage.type().name());

        if (pushMessage.referenceId() != null) {
            messageBuilder.putData("referenceId", pushMessage.referenceId());
        }

        return firebaseMessaging.send(messageBuilder.build());
    }
}
