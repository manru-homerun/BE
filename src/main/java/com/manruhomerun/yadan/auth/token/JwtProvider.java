package com.manruhomerun.yadan.auth.token;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.manruhomerun.yadan.auth.error.AuthErrorCode;
import com.manruhomerun.yadan.auth.error.exception.AuthException;
import com.manruhomerun.yadan.auth.properties.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider { // JWT 발급, 서명, 검증
    private static final String ISSUER = "yadan";
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final JwtProperties properties;
    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;

    // Base64 환경변수 Secret을 JWT 서명에 사용할 키로 변환
    public JwtProvider(JwtProperties properties) {
        this.properties = properties;
        this.accessSecretKey = createSecretKey(properties.getAccessSecret());
        this.refreshSecretKey = createSecretKey(properties.getRefreshSecret());
    }

    // AccessToken 발급
    public String issueAccessToken(String userId) {
        Instant issuedAt = Instant.now();
        Instant accessExpiresAt = issuedAt.plus(properties.getAccessExpiration());

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(userId)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(accessExpiresAt))
                .signWith(accessSecretKey)
                .compact();
    }

    // AccessToken, RefreshToken 발급
    public TokenPair issueTokenPair(String userId, String refreshTokenId) {
        Instant issuedAt = Instant.now();
        Instant refreshExpiresAt = issuedAt.plus(properties.getRefreshExpiration());

        String accessToken = issueAccessToken(userId);

        String refreshToken = Jwts.builder()
                .issuer(ISSUER)
                .subject(userId)
                .id(refreshTokenId)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(refreshExpiresAt))
                .signWith(refreshSecretKey)
                .compact();

        return new TokenPair(
                accessToken,
                refreshToken
        );
    }

    // AccessToken 검증 후 userId 반환
    public String verifyAccessToken(String accessToken) {
        Claims claims = parseClaims(
                accessToken,
                accessSecretKey,
                AuthErrorCode.INVALID_ACCESS_TOKEN
        );

        validateTokenType(claims, ACCESS_TOKEN_TYPE, AuthErrorCode.INVALID_ACCESS_TOKEN);

        String userId = claims.getSubject();
        if (userId == null || userId.isBlank()) {
            throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        return userId;
    }

    // RefreshToken 검증 후 userId, refreshTokenId 반환
    public RefreshTokenClaims verifyRefreshToken(String refreshToken) {
        Claims claims = parseClaims(
                refreshToken,
                refreshSecretKey,
                AuthErrorCode.INVALID_REFRESH_TOKEN
        );

        validateTokenType(claims, REFRESH_TOKEN_TYPE, AuthErrorCode.INVALID_REFRESH_TOKEN);

        String userId = claims.getSubject();
        String refreshTokenId = claims.getId();
        if (userId == null || userId.isBlank()
                || refreshTokenId == null || refreshTokenId.isBlank()) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        return new RefreshTokenClaims(userId, refreshTokenId);
    }

    // JWT 검증 후 내부 정보 반환
    private Claims parseClaims(
            String token,
            SecretKey secretKey,
            AuthErrorCode errorCode
    ) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthException(errorCode);
        }
    }

    // token type 검증
    private void validateTokenType(
            Claims claims,
            String expectedType,
            AuthErrorCode errorCode
    ) {
        String actualType = claims.get(TOKEN_TYPE_CLAIM, String.class);
        if (!expectedType.equals(actualType)) {
            throw new AuthException(errorCode);
        }
    }

    // 환경변수의 Base64 문자열을 JWT 서명에 사용할 SecretKey 객체로 변환
    private SecretKey createSecretKey(String base64Secret) {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("JWT Secret 설정이 올바르지 않습니다.", exception);
        }
    }
}
