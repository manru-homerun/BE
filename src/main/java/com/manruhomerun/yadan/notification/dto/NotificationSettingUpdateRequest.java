package com.manruhomerun.yadan.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record NotificationSettingUpdateRequest(
        @NotNull(message = "전체 알림 활성화 여부는 필수입니다.")
        @Schema(description = "전체 알림 활성화 여부", example = "true")
        Boolean notificationEnabled,

        @NotNull(message = "예매 일자 알림 활성화 여부는 필수입니다.")
        @Schema(description = "예매 일자 알림 활성화 여부", example = "true")
        Boolean ticketOpenNotificationEnabled,

        @NotNull(message = "방문 인증 리마인드 활성화 여부는 필수입니다.")
        @Schema(description = "방문 인증 리마인드 활성화 여부", example = "true")
        Boolean visitVerificationReminderEnabled,

        @NotNull(message = "지역 경기 알림 활성화 여부는 필수입니다.")
        @Schema(description = "지역 경기 알림 활성화 여부", example = "false")
        Boolean nearbyGameNotificationEnabled
) {
}
