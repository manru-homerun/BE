package com.manruhomerun.yadan.notification.service;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.notification.client.FirebasePushClient;
import com.manruhomerun.yadan.notification.domain.entity.PushInstallation;
import com.manruhomerun.yadan.notification.dto.NotificationTestPushRequest;
import com.manruhomerun.yadan.notification.dto.NotificationTestPushResponse;
import com.manruhomerun.yadan.notification.error.exception.PushInstallationNotFoundException;
import com.manruhomerun.yadan.notification.repository.PushInstallationRepository;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = {"firebase.enabled", "notification.test-api-enabled"},
        havingValue = "true"
)
public class NotificationTestPushService {

    private final UserRepository userRepository;
    private final PushInstallationRepository pushInstallationRepository;
    private final FirebasePushClient firebasePushClient;

    @Transactional
    public NotificationTestPushResponse send(
            String userId,
            NotificationTestPushRequest request
    ) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<PushInstallation> installations = pushInstallationRepository.findAllByUserId(userId);
        if (installations.isEmpty()) {
            throw new PushInstallationNotFoundException();
        }

        int successCount = 0;
        int failureCount = 0;

        for (PushInstallation installation : installations) {
            try {
                String messageId = firebasePushClient.sendTest(
                        installation.getFirebaseInstallationId(),
                        request.title(),
                        request.body(),
                        request.notificationId(),
                        request.type(),
                        request.referenceId(),
                        request.dryRun()
                );
                successCount++;

                log.info(
                        "FCM 테스트 푸시 요청에 성공했습니다. userId={}, installationId={}, dryRun={}, messageId={}",
                        userId,
                        installation.getId(),
                        request.dryRun(),
                        messageId
                );
            } catch (FirebaseMessagingException exception) {
                failureCount++;
                handleFailure(userId, installation, request.dryRun(), exception);
            }
        }

        return new NotificationTestPushResponse(
                request.dryRun(),
                installations.size(),
                successCount,
                failureCount
        );
    }

    private void handleFailure(
            String userId,
            PushInstallation installation,
            boolean dryRun,
            FirebaseMessagingException exception
    ) {
        if (exception.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
            pushInstallationRepository.delete(installation);
        }

        log.error(
                "FCM 테스트 푸시 요청에 실패했습니다. userId={}, installationId={}, dryRun={}, errorCode={}",
                userId,
                installation.getId(),
                dryRun,
                exception.getMessagingErrorCode(),
                exception
        );
    }
}
