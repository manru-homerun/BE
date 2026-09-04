package com.manruhomerun.yadan.notification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.notification.domain.entity.NotificationSetting;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    Optional<NotificationSetting> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
