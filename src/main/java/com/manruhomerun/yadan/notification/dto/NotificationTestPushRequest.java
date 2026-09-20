package com.manruhomerun.yadan.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

        @NotNull(message = "dryRun 여부는 필수입니다.")
        @Schema(
                description = "true이면 실제 전송 없이 FCM 검증만 수행",
                example = "true",
                defaultValue = "true"
        )
        Boolean dryRun
) {
}
