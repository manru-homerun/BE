package com.manruhomerun.yadan.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "서비스 API 인증에 사용하는 액세스 토큰", example = "service_access_token")
        String accessToken,
        @Schema(description = "서비스 액세스 토큰 재발급에 사용하는 리프레시 토큰", example = "service_refresh_token")
        String refreshToken,
        @Schema(description = "신규 회원 여부", example = "false")
        boolean isNewMember
) {
}
