package com.manruhomerun.yadan.auth.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.manruhomerun.yadan.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "login_session", // 로그인 단위로 발급된 refreshToken과 만료/폐기 상태
    indexes = {
        @Index(name = "idx_login_session_user_id", columnList = "user_id"),
        @Index(name = "idx_login_session_expires_at", columnList = "expires_at")
    }
)
public class LoginSession {

    private static final ZoneId SERVICE_ZONE_ID = ZoneId.of("Asia/Seoul");

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token_hash", nullable = false, unique = true, length = 64)
    private String refreshTokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt; // 폐기한 시각

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_used_at", nullable = false)
    private LocalDateTime lastUsedAt;

    private LoginSession(
            String sessionId,
            User user,
            String refreshTokenHash,
            LocalDateTime expiresAt
    ) {
        LocalDateTime now = LocalDateTime.now(SERVICE_ZONE_ID);

        this.id = sessionId;
        this.user = user;
        this.refreshTokenHash = refreshTokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = now;
        this.lastUsedAt = now;
    }

    public static LoginSession create(
            String sessionId,
            User user,
            String refreshTokenHash,
            LocalDateTime expiresAt
    ) {
        return new LoginSession(sessionId, user, refreshTokenHash, expiresAt);
    }

    public void rotate(String refreshTokenHash, LocalDateTime expiresAt) {
        this.refreshTokenHash = refreshTokenHash;
        this.expiresAt = expiresAt;
        this.lastUsedAt = LocalDateTime.now(SERVICE_ZONE_ID);
    }

    public void revoke() {
        if (revokedAt == null) {
            revokedAt = LocalDateTime.now(SERVICE_ZONE_ID);
        }
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return !expiresAt.isAfter(LocalDateTime.now(SERVICE_ZONE_ID));
    }
}
