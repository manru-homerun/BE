package com.manruhomerun.yadan.friend.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.manruhomerun.yadan.friend.domain.enums.FriendRequestStatus;
import com.manruhomerun.yadan.user.domain.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "friend_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_friend_requests_user_pair",
                        columnNames = {"first_user_id", "second_user_id"}
                )
        }
)
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_user_id", nullable = false)
    private User firstUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_user_id", nullable = false)
    private User secondUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requesterUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FriendRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        if (status == null) status = FriendRequestStatus.PENDING;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void accept() {
        status = FriendRequestStatus.ACCEPTED;
    }

    public void reject() {
        status = FriendRequestStatus.REJECTED;
    }

    public void cancel() {
        status = FriendRequestStatus.CANCELLED;
    }

    // 친구 재요청
    public void requestAgain(User requesterUser) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.requesterUser = requesterUser;
        this.status = FriendRequestStatus.PENDING;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public User getReceiverUser() {
        return requesterUser.getId().equals(firstUser.getId())
                ? secondUser
                : firstUser;
    }
}
