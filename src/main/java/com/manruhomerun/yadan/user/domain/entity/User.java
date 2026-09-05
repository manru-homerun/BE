package com.manruhomerun.yadan.user.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.manruhomerun.yadan.user.domain.enums.Gender;
import com.manruhomerun.yadan.user.domain.enums.UserProvider;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_nickname",
                        columnNames = "nickname"
                ),
            @UniqueConstraint(
                name = "uk_user_provider_provider_user_id",
                columnNames = {"provider", "provider_user_id"}
            )
        }
)
public class User {

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_team")
    private BaseballTeam favoriteTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    private UserProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @Column(length = 12, columnDefinition = "varchar(12) collate utf8mb4_bin")
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private Gender gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "onboarding_completed", nullable = false)
    private Boolean onboardingCompleted;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static User createOAuthUser(
            UserProvider provider,
            String providerUserId,
            String profileImageUrl
    ) {
        return User.builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .profileImageUrl(profileImageUrl)
                .onboardingCompleted(false)
                .isDeleted(false)
                .build();
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 사용자 PK는 UUID 문자열을 기본값으로 채운다.
        if (id == null) id = UUID.randomUUID().toString();
        if (onboardingCompleted == null) onboardingCompleted = false;
        if (isDeleted == null) isDeleted = false;

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void completeOnboarding(
            String nickname,
            Gender gender,
            LocalDate birthday,
            BaseballTeam favoriteTeam
    ) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.favoriteTeam = favoriteTeam;
        this.onboardingCompleted = true;
    }

    public void updateProfile(
            String profileImageUrl,
            String nickname,
            BaseballTeam favoriteTeam,
            LocalDate birthday,
            Gender gender
    ) {
        this.profileImageUrl = profileImageUrl;
        this.nickname = nickname;
        this.favoriteTeam = favoriteTeam;
        this.birthday = birthday;
        this.gender = gender;
    }
}
