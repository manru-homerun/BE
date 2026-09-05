package com.manruhomerun.yadan.notification.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.manruhomerun.yadan.notification.domain.enums.NotificationType;
import com.manruhomerun.yadan.user.domain.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notifications_user_created_at",
                        columnList = "user_id, created_at"
                )
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "body", nullable = false, length = 500)
    private String body;

    @Column(name = "reference_id", length = 255)
    private String referenceId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Notification create(
            User user,
            NotificationType type,
            String title,
            String body,
            String referenceId
    ) {
        return Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .body(body)
                .referenceId(referenceId)
                .build();
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
