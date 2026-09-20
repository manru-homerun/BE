package com.manruhomerun.yadan.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record NotificationSettingUpdateRequest(
        @NotNull(message = "친구 관련 알림 활성화 여부는 필수입니다.")
        @Schema(description = "친구 관련 알림 활성화 여부", example = "true")
        Boolean friendNotificationEnabled,

        @NotNull(message = "주간 경기 일정 알림 활성화 여부는 필수입니다.")
        @Schema(description = "주간 경기 일정 알림 활성화 여부", example = "true")
        Boolean weeklyTeamScheduleNotificationEnabled
) {
}
