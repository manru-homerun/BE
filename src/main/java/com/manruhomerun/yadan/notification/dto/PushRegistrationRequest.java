package com.manruhomerun.yadan.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "FCM 푸시 수신을 위한 앱 설치 정보 등록 요청")
public record PushRegistrationRequest(
        @NotBlank(message = "Firebase Installation ID는 필수입니다.")
        @Size(max = 255, message = "Firebase Installation ID는 255자 이하여야 합니다.")
        @Schema(
                description = "Android 앱의 Firebase Installation ID(FID)",
                example = "cV9mQ1AbCdEfGhIjKlMnOp"
        )
        String installationId,

        @Size(max = 50, message = "앱 버전은 50자 이하여야 합니다.")
        @Schema(
                description = "현재 설치된 앱 버전(선택)",
                example = "1.0.0",
                nullable = true
        )
        String appVersion
) {
}
