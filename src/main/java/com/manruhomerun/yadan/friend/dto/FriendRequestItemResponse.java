package com.manruhomerun.yadan.friend.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;
import com.manruhomerun.yadan.user.domain.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "친구 요청 목록 항목 응답")
public record FriendRequestItemResponse(
        @Schema(description = "친구 요청 ID", example = "501")
        Long friendRequestId,

        @Schema(description = "상대방 사용자 ID", example = "33333333-3333-3333-3333-333333333333")
        String userId,

        @Schema(description = "상대방 닉네임", example = "서연")
        String nickname,

        @Schema(description = "상대방 프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "상대방의 응원팀 ID", example = "3")
        Long favoriteTeamId,

        @Schema(description = "상대방의 응원팀 이름", example = "롯데 자이언츠")
        String favoriteTeamName
) {
    public static FriendRequestItemResponse fromRequester(FriendRequest friendRequest) {
        return from(friendRequest, friendRequest.getRequesterUser());
    }

    public static FriendRequestItemResponse fromReceiver(FriendRequest friendRequest) {
        return from(friendRequest, friendRequest.getReceiverUser());
    }

    private static FriendRequestItemResponse from(FriendRequest friendRequest, User user) {
        BaseballTeam favoriteTeam = user.getFavoriteTeam();

        return new FriendRequestItemResponse(
                friendRequest.getId(),
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                favoriteTeam == null ? null : favoriteTeam.getId(),
                favoriteTeam == null ? null : favoriteTeam.getTeamName()
        );
    }
}
