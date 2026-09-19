package com.manruhomerun.yadan.user.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.friend.domain.enums.FriendRelationshipStatus;
import com.manruhomerun.yadan.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 검색 결과")
public record UserSearchItemResponse(

        @Schema(description = "사용자 ID", example = "22222222-2222-2222-2222-222222222222")
        String userId,

        @Schema(description = "사용자 닉네임", example = "야구여행자")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png")
        String profileImageUrl,

        @Schema(description = "응원 구단 이름", example = "롯데 자이언츠")
        String favoriteTeamName,

        @Schema(description = "현재 사용자와의 친구 관계 상태", example = "REQUEST_SENT")
        FriendRelationshipStatus friendStatus

) {

    public static UserSearchItemResponse from(
            User user,
            FriendRelationshipStatus friendStatus
    ) {
        BaseballTeam favoriteTeam = user.getFavoriteTeam();

        return new UserSearchItemResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                favoriteTeam == null ? null : favoriteTeam.getTeamName(),
                friendStatus
        );
    }
}
