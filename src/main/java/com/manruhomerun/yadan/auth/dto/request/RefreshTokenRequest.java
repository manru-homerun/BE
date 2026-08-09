package com.manruhomerun.yadan.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        @Schema(
                description = "서비스에서 발급한 리프레시 토큰",
                example = "service_refresh_token"
        )
        String refreshToken
) {
}
