package com.manruhomerun.yadan.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.notification.domain.entity.Notification;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderByCreatedAtDescIdDesc(String userId);

    boolean existsByUserIdAndTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            String userId,
            NotificationType type,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}
