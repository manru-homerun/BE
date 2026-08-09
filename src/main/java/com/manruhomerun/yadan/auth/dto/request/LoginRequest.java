package com.manruhomerun.yadan.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "카카오 액세스 토큰은 필수입니다.")
        @Schema(
                description = "Android Kakao SDK에서 발급받은 카카오 액세스 토큰",
                example = "kakao_access_token"
        )
        String kakaoAccessToken
) {
}
