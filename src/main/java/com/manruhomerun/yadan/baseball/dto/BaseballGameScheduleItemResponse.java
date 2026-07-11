package com.manruhomerun.yadan.baseball.dto;

import java.time.LocalDateTime;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;

import io.swagger.v3.oas.annotations.media.Schema;

public record BaseballGameScheduleItemResponse(
        @Schema(description = "경기 ID", example = "1001")
        Long gameId,
        @Schema(description = "경기 일시", example = "2026-06-12T14:30:00")
        LocalDateTime dateTime,
        @Schema(description = "원정 팀 정보")
        TeamSummary awayTeam,
        @Schema(description = "홈 팀 정보")
        TeamSummary homeTeam
) {

    public static BaseballGameScheduleItemResponse from(BaseballGame game) {
        return new BaseballGameScheduleItemResponse(
                game.getId(),
                game.getGameDate(),
                TeamSummary.from(game.getAwayTeam()),
                TeamSummary.from(game.getHomeTeam())
        );
    }

    public record TeamSummary(
            @Schema(description = "팀 ID", example = "1")
            Long teamId,
            @Schema(description = "팀 이름", example = "삼성 라이온즈")
            String teamName,
            @Schema(description = "팀 로고 이미지 URL", example = "https://example.com/samsung_lions.png")
            String teamLogo
    ) {
        public static TeamSummary from(BaseballTeam team) {
            return new TeamSummary(team.getId(), team.getTeamName(), team.getLogoImage());
        }
    }
}
