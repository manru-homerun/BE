package com.manruhomerun.yadan.auth.token;

// 검증된 Refresh Token에서 추출한 사용자 ID와 토큰 ID를 담는 객체
public record RefreshTokenClaims(
        String userId,
        String refreshTokenId
) {
}
