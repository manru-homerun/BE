package com.manruhomerun.yadan.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.notification.domain.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderByCreatedAtDescIdDesc(String userId);
}
