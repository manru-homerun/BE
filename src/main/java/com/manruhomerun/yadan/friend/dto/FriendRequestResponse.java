package com.manruhomerun.yadan.friend.dto;

import java.time.LocalDateTime;

import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;
import com.manruhomerun.yadan.friend.domain.enums.FriendRequestStatus;

import io.swagger.v3.oas.annotations.media.Schema;

// 친구 요청 응답 DTO
public record FriendRequestResponse(
        @Schema(description = "친구 요청 ID", example = "1")
        Long id,

        @Schema(
                description = "요청자 ID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        String requesterUserId,

        @Schema(description = "요청자 닉네임", example = "야구팬1")
        String requesterNickname,

        @Schema(description = "요청자 프로필 이미지 URL")
        String requesterProfileImageUrl,

        @Schema(
                description = "수신자 사용자 ID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        String receiverUserId,

        @Schema(description = "수신자 닉네임", example = "야구팬2")
        String receiverNickname,

        @Schema(description = "수신자 프로필 이미지 URL")
        String receiverProfileImageUrl,

        @Schema(description = "친구 요청 상태", example = "PENDING")
        FriendRequestStatus status,

        @Schema(description = "친구 요청 생성 일시")
        LocalDateTime createdAt,

        @Schema(description = "친구 요청 수정 일시")
        LocalDateTime updatedAt
) {
    public static FriendRequestResponse from(FriendRequest friendRequest) {
        return new FriendRequestResponse(
                friendRequest.getId(),
                friendRequest.getRequesterUser().getId(),
                friendRequest.getRequesterUser().getNickname(),
                friendRequest.getRequesterUser().getProfileImageUrl(),
                friendRequest.getReceiverUser().getId(),
                friendRequest.getReceiverUser().getNickname(),
                friendRequest.getReceiverUser().getProfileImageUrl(),
                friendRequest.getStatus(),
                friendRequest.getCreatedAt(),
                friendRequest.getUpdatedAt()
        );
    }
}
