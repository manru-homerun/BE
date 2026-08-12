package com.manruhomerun.yadan.friend.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.friend.domain.entity.Friend;
import com.manruhomerun.yadan.user.domain.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendResponse(
        @Schema(description = "친구 관계 ID", example = "301")
        Long friendId,

        @Schema(
                description = "친구 사용자 ID",
                example = "22222222-2222-2222-2222-222222222222"
        )
        String userId,

        @Schema(description = "친구 닉네임", example = "야구팬2")
        String nickname,

        @Schema(description = "친구 프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "친구의 응원팀 ID", example = "1")
        Long favoriteTeamId,

        @Schema(description = "친구의 응원팀 이름", example = "LG 트윈스")
        String favoriteTeamName
) {
    public static FriendResponse from(Friend friend, String currentUserId) {
        User friendUser = friend.getFirstUser().getId().equals(currentUserId)
                ? friend.getSecondUser()
                : friend.getFirstUser();
        BaseballTeam favoriteTeam = friendUser.getFavoriteTeam();

        return new FriendResponse(
                friend.getId(),
                friendUser.getId(),
                friendUser.getNickname(),
                friendUser.getProfileImageUrl(),
                favoriteTeam == null ? null : favoriteTeam.getId(),
                favoriteTeam == null ? null : favoriteTeam.getTeamName()
        );
    }
}
