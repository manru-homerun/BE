package com.manruhomerun.yadan.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshTokenResponse(
        @Schema(
                description = "새로 발급된 서비스 액세스 토큰",
                example = "new_service_access_token"
        )
        String accessToken,

        @Schema(
                description = "새로 발급된 서비스 리프레시 토큰",
                example = "new_service_refresh_token"
        )
        String refreshToken
) {
}
