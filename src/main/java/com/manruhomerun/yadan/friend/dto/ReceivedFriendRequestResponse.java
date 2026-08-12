package com.manruhomerun.yadan.friend.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;
import com.manruhomerun.yadan.user.domain.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "받은 친구 요청 응답")
public record ReceivedFriendRequestResponse(
        @Schema(description = "친구 요청 ID", example = "501")
        Long friendRequestId,

        @Schema(description = "요청자 사용자 ID", example = "33333333-3333-3333-3333-333333333333")
        String userId,

        @Schema(description = "요청자 닉네임", example = "서연")
        String nickname,

        @Schema(description = "요청자 프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "요청자의 응원팀 ID", example = "3")
        Long favoriteTeamId,

        @Schema(description = "요청자의 응원팀 이름", example = "롯데 자이언츠")
        String favoriteTeamName
) {
    public static ReceivedFriendRequestResponse from(FriendRequest friendRequest) {
        User requester = friendRequest.getRequesterUser();
        BaseballTeam favoriteTeam = requester.getFavoriteTeam();

        return new ReceivedFriendRequestResponse(
                friendRequest.getId(),
                requester.getId(),
                requester.getNickname(),
                requester.getProfileImageUrl(),
                favoriteTeam == null ? null : favoriteTeam.getId(),
                favoriteTeam == null ? null : favoriteTeam.getTeamName()
        );
    }
}
