package com.manruhomerun.yadan.baseball.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballStadium;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;

import io.swagger.v3.oas.annotations.media.Schema;

public record BaseballGameDetailResponse(
        @Schema(description = "경기 ID", example = "1001")
        Long gameId,
        @Schema(description = "경기 일시", example = "2026-06-12 14:30")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime dateTime,
        @Schema(description = "원정 팀 정보")
        TeamSummary awayTeam,
        @Schema(description = "홈 팀 정보")
        TeamSummary homeTeam,
        @Schema(description = "경기장 정보")
        StadiumSummary stadium,
        @Schema(description = "경기 타입", example = "REGULAR")
        String type,
        @Schema(description = "취소 여부, 예정 경기인 경우 null", example = "false", nullable = true)
        Boolean canceled,
        @Schema(description = "경기 결과, 예정 경기인 경우 null", nullable = true)
        GameResult gameResult
) {

    public static BaseballGameDetailResponse from(BaseballGame game) {
        return new BaseballGameDetailResponse(
                game.getId(),
                game.getGameDate(),
                TeamSummary.from(game.getAwayTeam()),
                TeamSummary.from(game.getHomeTeam()),
                StadiumSummary.from(game.getStadium()),
                game.getGameType().name(),
                game.getIsCanceled(),
                GameResult.from(game)
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

    public record StadiumSummary(
            @Schema(description = "구장 ID", example = "10")
            Long stadiumId,
            @Schema(description = "구장 이름", example = "잠실야구장")
            String stadiumName,
            @Schema(description = "구장 위도", example = "37.5122")
            BigDecimal latitude,
            @Schema(description = "구장 경도", example = "127.0719")
            BigDecimal longitude,
            @Schema(description = "경기장 지역 코드", example = "11000")
            String regionCode
    ) {
        public static StadiumSummary from(BaseballStadium stadium) {
            return new StadiumSummary(
                    stadium.getId(),
                    stadium.getName(),
                    stadium.getLatitude(),
                    stadium.getLongitude(),
                    stadium.getRegionCode()
            );
        }
    }

    public record GameResult(
            @Schema(description = "원정 팀 점수", example = "3")
            Integer awayTeamScore,
            @Schema(description = "홈 팀 점수", example = "4")
            Integer homeTeamScore
    ) {
        public static GameResult from(BaseballGame game) {
            if (game.getAwayTeamScore() == null || game.getHomeTeamScore() == null) {
                return null;
            }

            return new GameResult(game.getAwayTeamScore(), game.getHomeTeamScore());
        }
    }
}
