package com.manruhomerun.yadan.notification.dto;

import java.time.LocalDateTime;

import com.manruhomerun.yadan.notification.domain.entity.Notification;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 목록 항목 응답")
public record NotificationResponse(
        @Schema(description = "알림 ID", example = "1")
        Long notificationId,

        @Schema(description = "알림 종류", example = "FRIEND_REQUEST")
        NotificationType type,

        @Schema(description = "알림 제목", example = "친구 신청")
        String title,

        @Schema(description = "알림 내용", example = "새로운 친구 신청이 도착했습니다.")
        String body,

        @Schema(
                description = "알림 클릭 시 조회할 대상 리소스 ID",
                example = "123",
                nullable = true
        )
        String referenceId,

        @Schema(description = "알림 생성 시각", example = "2026-09-05T14:30:00")
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getReferenceId(),
                notification.getCreatedAt()
        );
    }
}
