package com.manruhomerun.yadan.notification.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.manruhomerun.yadan.user.domain.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "push_installations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_push_installations_firebase_installation_id",
                        columnNames = "firebase_installation_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_push_installations_user_id",
                        columnList = "user_id"
                )
        }
)
public class PushInstallation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "firebase_installation_id", nullable = false, length = 255)
    private String firebaseInstallationId;

    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static PushInstallation create(
            User user,
            String firebaseInstallationId,
            String appVersion
    ) {
        return PushInstallation.builder()
                .user(user)
                .firebaseInstallationId(firebaseInstallationId)
                .appVersion(appVersion)
                .build();
    }

    public void updateRegistration(User user, String appVersion) {
        this.user = user;
        this.appVersion = appVersion;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
