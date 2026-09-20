package com.manruhomerun.yadan.notification.dto;

import com.manruhomerun.yadan.notification.domain.enums.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "FCM 테스트 푸시 전송 요청")
public record NotificationTestPushRequest(
        @NotBlank(message = "테스트 알림 제목은 필수입니다.")
        @Size(max = 100, message = "테스트 알림 제목은 100자 이하여야 합니다.")
        @Schema(description = "테스트 알림 제목", example = "FCM 테스트")
        String title,

        @NotBlank(message = "테스트 알림 내용은 필수입니다.")
        @Size(max = 500, message = "테스트 알림 내용은 500자 이하여야 합니다.")
        @Schema(description = "테스트 알림 내용", example = "Firebase 연결 테스트 알림입니다.")
        String body,

        @NotNull(message = "알림 ID는 필수입니다.")
        @Positive(message = "알림 ID는 양수여야 합니다.")
        @Schema(description = "FCM data에 문자열로 전달할 테스트 알림 ID", example = "1001")
        Long notificationId,

        @NotNull(message = "알림 타입은 필수입니다.")
        @Schema(
                description = "알림 클릭 시 앱 화면 이동에 사용할 타입",
                example = "WEEKLY_TEAM_SCHEDULE",
                allowableValues = {
                        "FRIEND_REQUEST",
                        "FRIEND_REQUEST_ACCEPTED",
                        "WEEKLY_TEAM_SCHEDULE"
                }
        )
        NotificationType type,

        @Size(max = 255, message = "참조 ID는 255자 이하여야 합니다.")
        @Schema(
                description = "알림 클릭 시 조회할 대상 리소스 ID",
                example = "2",
                nullable = true
        )
        String referenceId,

        @NotNull(message = "dryRun 여부는 필수입니다.")
        @Schema(
                description = "true이면 실제 전송 없이 FCM 검증만 수행",
                example = "true",
                defaultValue = "true"
        )
        Boolean dryRun
) {
}
