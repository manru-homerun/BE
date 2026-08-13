package com.manruhomerun.yadan.user.dto;

import java.time.LocalDate;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "나의 프로필 조회 응답")
public record UserProfileResponse(

        @Schema(description = "사용자 ID", example = "11111111-1111-1111-1111-111111111111")
        String userId,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png")
        String profileImageUrl,

        @Schema(description = "닉네임", example = "야구좋아")
        String nickname,

        @Schema(description = "응원 구단 정보")
        FavoriteTeamResponse favoriteTeam,

        @Schema(description = "생년월일", example = "1998-07-15")
        LocalDate birthday,

        @Schema(description = "성별", example = "MALE")
        Gender gender

) {

    public static UserProfileResponse from(User user) {
        BaseballTeam favoriteTeam = user.getFavoriteTeam();

        return new UserProfileResponse(
                user.getId(),
                user.getProfileImageUrl(),
                user.getNickname(),
                favoriteTeam == null ? null : FavoriteTeamResponse.from(favoriteTeam),
                user.getBirthday(),
                user.getGender()
        );
    }
}
