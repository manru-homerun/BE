package com.manruhomerun.yadan.notification.dto;

import com.manruhomerun.yadan.notification.domain.entity.NotificationSetting;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationSettingResponse(
        @Schema(description = "친구 신청 수신 / 친구 수락 알림 활성화 여부", example = "true")
        Boolean friendNotificationEnabled,

        @Schema(description = "주간 경기 일정 알림 활성화 여부", example = "true")
        Boolean weeklyTeamScheduleNotificationEnabled
) {

    public static NotificationSettingResponse from(NotificationSetting notificationSetting) {
        return new NotificationSettingResponse(
                notificationSetting.getFriendNotificationEnabled(),
                notificationSetting.getWeeklyTeamScheduleNotificationEnabled()
        );
    }
}
