package com.manruhomerun.yadan.friend.dto;

import java.time.LocalDateTime;

import com.manruhomerun.yadan.friend.domain.entity.Friend;
import com.manruhomerun.yadan.user.domain.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendResponse(
        @Schema(description = "친구 관계 ID", example = "1")
        Long id,

        @Schema(
                description = "친구 사용자 ID",
                example = "22222222-2222-2222-2222-222222222222"
        )
        String userId,

        @Schema(description = "친구 닉네임", example = "야구팬2")
        String nickname,

        @Schema(description = "친구 프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "친구가 된 일시")
        LocalDateTime createdAt
) {
    public static FriendResponse from(Friend friend, String currentUserId) {
        User friendUser = friend.getUser().getId().equals(currentUserId)
                ? friend.getFriendUser()
                : friend.getUser();

        return new FriendResponse(
                friend.getId(),
                friendUser.getId(),
                friendUser.getNickname(),
                friendUser.getProfileImageUrl(),
                friend.getCreatedAt()
        );
    }
}
