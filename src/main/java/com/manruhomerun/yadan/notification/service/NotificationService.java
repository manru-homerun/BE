package com.manruhomerun.yadan.notification.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.notification.domain.entity.Notification;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;
import com.manruhomerun.yadan.notification.dto.NotificationResponse;
import com.manruhomerun.yadan.notification.event.NotificationCreatedEvent;
import com.manruhomerun.yadan.notification.repository.NotificationRepository;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<NotificationResponse> getNotifications(String userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return notificationRepository.findAllByUserIdOrderByCreatedAtDescIdDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public Long createNotification(
            User recipient,
            NotificationType type,
            String title,
            String body,
            String referenceId
    ) {
        Notification notification = Notification.create(
                recipient,
                type,
                title,
                body,
                referenceId
        );

        Notification savedNotification = notificationRepository.save(notification);
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification.getId());

        // 알림 생성 이벤트 발행
        // AFTER_COMMIT Listener가 트랜잭션 커밋 성공 후 처리
        eventPublisher.publishEvent(event);
        return savedNotification.getId();
    }
}
