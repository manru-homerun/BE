package com.manruhomerun.yadan.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.notification.domain.entity.NotificationSetting;
import com.manruhomerun.yadan.notification.dto.NotificationSettingResponse;
import com.manruhomerun.yadan.notification.dto.NotificationSettingUpdateRequest;
import com.manruhomerun.yadan.notification.error.exception.NotificationSettingNotFoundException;
import com.manruhomerun.yadan.notification.repository.NotificationSettingRepository;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationSettingService {

    private final UserRepository userRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    public NotificationSettingResponse getSettings(String userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        NotificationSetting notificationSetting = notificationSettingRepository.findByUserId(userId)
                .orElseThrow(NotificationSettingNotFoundException::new);

        return NotificationSettingResponse.from(notificationSetting);
    }

    @Transactional
    public void updateSettings(String userId, NotificationSettingUpdateRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        NotificationSetting notificationSetting = notificationSettingRepository.findByUserId(userId)
                .orElseThrow(NotificationSettingNotFoundException::new);

        notificationSetting.update(
                request.notificationEnabled(),
                request.ticketOpenNotificationEnabled(),
                request.visitVerificationReminderEnabled(),
                request.nearbyGameNotificationEnabled()
        );
    }
}
