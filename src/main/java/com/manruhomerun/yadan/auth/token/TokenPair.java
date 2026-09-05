package com.manruhomerun.yadan.auth.token;

// JwtProvider가 발급한 Access Token과 Refresh Token 정보를 담는 객체
public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
