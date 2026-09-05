package com.manruhomerun.yadan.notification.service;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.manruhomerun.yadan.notification.client.FirebasePushClient;
import com.manruhomerun.yadan.notification.client.PushMessage;
import com.manruhomerun.yadan.notification.domain.entity.Notification;
import com.manruhomerun.yadan.notification.domain.entity.NotificationSetting;
import com.manruhomerun.yadan.notification.domain.entity.PushInstallation;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;
import com.manruhomerun.yadan.notification.repository.NotificationRepository;
import com.manruhomerun.yadan.notification.repository.NotificationSettingRepository;
import com.manruhomerun.yadan.notification.repository.PushInstallationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
public class NotificationPushService { // 설정과 전송 대상을 확인하고 전송 지시

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final PushInstallationRepository pushInstallationRepository;
    private final FirebasePushClient firebasePushClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Long notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (notificationOptional.isEmpty()) {
            log.warn("푸시 전송 대상 알림을 찾을 수 없습니다. notificationId={}", notificationId);
            return;
        }

        Notification notification = notificationOptional.get();
        String userId = notification.getUser().getId();

        Optional<NotificationSetting> settingOptional = notificationSettingRepository.findByUserId(userId);
        if (settingOptional.isEmpty()) {
            log.warn("푸시 전송 대상 사용자의 알림 설정을 찾을 수 없습니다. userId={}", userId);
            return;
        }

        if (!isPushEnabled(settingOptional.get(), notification.getType())) {
            return;
        }

        List<PushInstallation> installations = pushInstallationRepository.findAllByUserId(userId);
        PushMessage pushMessage = PushMessage.from(notification);

        for (PushInstallation installation : installations) {
            sendToInstallation(installation, pushMessage, notificationId);
        }
    }

    private boolean isPushEnabled(
            NotificationSetting setting,
            NotificationType notificationType
    ) {
        if (!Boolean.TRUE.equals(setting.getNotificationEnabled())) {
            return false;
        }

        return switch (notificationType) {
            case FRIEND_REQUEST, FRIEND_REQUEST_ACCEPTED -> true;
            case TICKET_OPEN -> Boolean.TRUE.equals(setting.getTicketOpenNotificationEnabled());
            case VISIT_VERIFICATION_REMINDER ->
                    Boolean.TRUE.equals(setting.getVisitVerificationReminderEnabled());
            case NEARBY_GAME -> Boolean.TRUE.equals(setting.getNearbyGameNotificationEnabled());
        };
    }

    private void sendToInstallation(
            PushInstallation installation,
            PushMessage pushMessage,
            Long notificationId
    ) {
        try {
            String messageId = firebasePushClient.send(
                    installation.getFirebaseInstallationId(),
                    pushMessage
            );

            log.debug(
                    "FCM 푸시 전송 성공. notificationId={}, installationId={}, messageId={}",
                    notificationId,
                    installation.getId(),
                    messageId
            );
        } catch (FirebaseMessagingException exception) {
            if (exception.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                pushInstallationRepository.delete(installation);
                log.info(
                        "FCM에 등록되지 않은 앱 설치 정보를 삭제했습니다. installationId={}",
                        installation.getId()
                );
                return;
            }

            log.error(
                    "FCM 푸시 전송에 실패했습니다. notificationId={}, installationId={}, errorCode={}",
                    notificationId,
                    installation.getId(),
                    exception.getMessagingErrorCode(),
                    exception
            );
        }
    }
}
