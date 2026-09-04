package com.manruhomerun.yadan.notification.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.hibernate.annotations.ColumnDefault;

import com.manruhomerun.yadan.user.domain.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
        name = "notification_settings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_settings_user",
                        columnNames = "user_id"
                )
        }
)
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notification_enabled", nullable = false)
    @ColumnDefault("true")
    private Boolean notificationEnabled;

    @Column(name = "ticket_open_notification_enabled", nullable = false)
    @ColumnDefault("true")
    private Boolean ticketOpenNotificationEnabled;

    @Column(name = "visit_verification_reminder_enabled", nullable = false)
    @ColumnDefault("true")
    private Boolean visitVerificationReminderEnabled;

    @Column(name = "nearby_game_notification_enabled", nullable = false)
    @ColumnDefault("false")
    private Boolean nearbyGameNotificationEnabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // TODO 인증 연동 시 신규 User 저장 직후 같은 트랜잭션에서 기본 알림 설정을 저장
    public static NotificationSetting createDefault(User user) {
        return NotificationSetting.builder()
                .user(user)
                .notificationEnabled(true)
                .ticketOpenNotificationEnabled(true)
                .visitVerificationReminderEnabled(true)
                .nearbyGameNotificationEnabled(true)
                .build();
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (notificationEnabled == null) notificationEnabled = true;
        if (ticketOpenNotificationEnabled == null) ticketOpenNotificationEnabled = true;
        if (visitVerificationReminderEnabled == null) visitVerificationReminderEnabled = true;
        if (nearbyGameNotificationEnabled == null) nearbyGameNotificationEnabled = true;

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
