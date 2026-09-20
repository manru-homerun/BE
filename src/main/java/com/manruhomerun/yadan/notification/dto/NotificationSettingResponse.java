package com.manruhomerun.yadan.notification.dto;

import com.manruhomerun.yadan.notification.domain.entity.NotificationSetting;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationSettingResponse(
        @Schema(description = "전체 알림 활성화 여부", example = "true")
        Boolean notificationEnabled,

        @Schema(description = "예매 일자 알림 활성화 여부", example = "true")
        Boolean ticketOpenNotificationEnabled,

        @Schema(description = "방문 인증 리마인드 활성화 여부", example = "true")
        Boolean visitVerificationReminderEnabled,

        @Schema(description = "지역 경기 알림 활성화 여부", example = "true")
        Boolean nearbyGameNotificationEnabled
) {

    public static NotificationSettingResponse from(NotificationSetting notificationSetting) {
        return new NotificationSettingResponse(
                notificationSetting.getNotificationEnabled(),
                notificationSetting.getTicketOpenNotificationEnabled(),
                notificationSetting.getVisitVerificationReminderEnabled(),
                notificationSetting.getNearbyGameNotificationEnabled()
        );
    }
}
