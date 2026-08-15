package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "여행 목록 응답")
public record TravelListResponse(
        @Schema(description = "여행 ID", example = "1e3a5081-675e-4264-8e56-ebb659e12acd")
        String id,

        @Schema(description = "여행 시작일", example = "2026-08-15")
        LocalDate from,

        @Schema(description = "여행 종료일", example = "2026-08-16")
        LocalDate to,

        @Schema(description = "여행 이름", example = "엘지트윈스짱")
        String name,

        @Schema(description = "지역 코드", example = "11000")
        String regionCode,

        @Schema(description = "현재 사용자의 방장 여부", example = "true")
        boolean isLeader,
        // TODO: 추가예정
        // boolean hasSticker,
        // boolean certifiedSpotsCount,
        @Schema(description = "등록된 여행지 수", example = "6")
        int spotsCount,

        @Schema(description = "직관 경기 정보")
        BaseballGameResponse baseballGame
) {
    public static TravelListResponse from(Travel travel, String userId) {
        List<User> users = travel.getTravelUserList().stream()
                .map(travelUser -> travelUser.getUser())
                .toList();

        boolean isLeader = travel.getTravelUserList().stream()
                .anyMatch(travelUser ->
                        userId.equals(travelUser.getUser().getId())
                                && travelUser.isLeader()
                );
        return new TravelListResponse(
                travel.getId(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getName(),
                travel.getRegionCode(),
                isLeader,
                // travel.getTravelTravelSpotList() != null && !travel.getTravelTravelSpotList().isEmpty(),
                travel.getTravelTravelSpotList() != null ? travel.getTravelTravelSpotList().size() : 0,
                BaseballGameResponse.from(travel.getBaseballGame())
        );
    }
    @Schema(description = "직관 경기 요약 정보")
    public record BaseballGameResponse(
            @Schema(description = "경기 ID", example = "1091")
            Long id,

            @Schema(description = "홈 팀 ID", example = "1")
            Long homeTeam,

            @Schema(description = "원정 팀 ID", example = "10")
            Long awayTeam
    ) {
        public static BaseballGameResponse from(BaseballGame baseballGame) {
            return new BaseballGameResponse(
                    baseballGame.getId(),
                    baseballGame.getHomeTeam().getId(),
                    baseballGame.getAwayTeam().getId()
            );
        }
    }
}
