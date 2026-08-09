package com.manruhomerun.yadan.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

// Refresh Token의 DB 저장, 비교를 위한 해시
@Component
public class TokenHasher {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final HexFormat HEX_FORMAT = HexFormat.of();

    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("해시할 토큰은 필수입니다.");
        }

        return HEX_FORMAT.formatHex(digest(rawToken));
    }

    public boolean matches(String rawToken, String storedTokenHash) {
        if (rawToken == null || rawToken.isBlank()
                || storedTokenHash == null || storedTokenHash.isBlank()) {
            return false;
        }

        try {
            byte[] actualHash = digest(rawToken);
            byte[] expectedHash = HEX_FORMAT.parseHex(storedTokenHash);
            return MessageDigest.isEqual(actualHash, expectedHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    // Refresh Token을 SHA-256 해시 바이트로 변환
    private byte[] digest(String rawToken) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            return messageDigest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 해시 알고리즘을 사용할 수 없습니다.", exception);
        }
    }
}
