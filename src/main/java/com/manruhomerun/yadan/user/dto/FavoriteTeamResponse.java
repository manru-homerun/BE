package com.manruhomerun.yadan.user.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자가 응원하는 구단 정보")
public record FavoriteTeamResponse(

        @Schema(description = "구단 ID", example = "1")
        Long teamId,

        @Schema(description = "구단명", example = "LG 트윈스")
        String teamName,

        @Schema(description = "구단 로고 이미지 URL", example = "https://example.com/lg-twins.png")
        String logoImage

) {

    public static FavoriteTeamResponse from(BaseballTeam team) {
        return new FavoriteTeamResponse(
                team.getId(),
                team.getTeamName(),
                team.getLogoImage()
        );
    }
}
