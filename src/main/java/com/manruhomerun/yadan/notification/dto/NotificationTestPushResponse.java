package com.manruhomerun.yadan.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "FCM 테스트 푸시 전송 결과")
public record NotificationTestPushResponse(
        @Schema(description = "dry-run 실행 여부", example = "true")
        boolean dryRun,

        @Schema(description = "전송 또는 검증을 시도한 앱 설치 수", example = "1")
        int attemptedCount,

        @Schema(description = "FCM 요청 성공 수", example = "1")
        int successCount,

        @Schema(description = "FCM 요청 실패 수", example = "0")
        int failureCount
) {
}
