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

    @Column(name = "friend_notification_enabled", nullable = false)
    @ColumnDefault("true")
    private Boolean friendNotificationEnabled;

    @Column(name = "weekly_team_schedule_notification_enabled", nullable = false)
    @ColumnDefault("true")
    private Boolean weeklyTeamScheduleNotificationEnabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static NotificationSetting createDefault(User user) {
        return NotificationSetting.builder()
                .user(user)
                .friendNotificationEnabled(true)
                .weeklyTeamScheduleNotificationEnabled(true)
                .build();
    }

    public void update(
            Boolean friendNotificationEnabled,
            Boolean weeklyTeamScheduleNotificationEnabled
    ) {
        this.friendNotificationEnabled = friendNotificationEnabled;
        this.weeklyTeamScheduleNotificationEnabled = weeklyTeamScheduleNotificationEnabled;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (friendNotificationEnabled == null) friendNotificationEnabled = true;
        if (weeklyTeamScheduleNotificationEnabled == null) {
            weeklyTeamScheduleNotificationEnabled = true;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
