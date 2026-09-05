package com.manruhomerun.yadan.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.notification.domain.entity.Notification;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;
import com.manruhomerun.yadan.notification.dto.NotificationResponse;
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

        return notificationRepository.save(notification).getId();
    }
}
