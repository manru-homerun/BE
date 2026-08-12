package com.manruhomerun.yadan.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "소셜 로그인 제공자의 액세스 토큰은 필수입니다.")
        @Schema(
                description = "소셜 로그인 제공자에서 발급받은 액세스 토큰",
                example = "provider_access_token"
        )
        String providerAccessToken
) {
}
