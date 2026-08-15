package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.user.domain.entity.User;

import java.time.LocalDate;
import java.util.List;

public record TravelListResponse(
        LocalDate from,
        LocalDate to,
        String name,
        String regionCode,
        boolean isLeader,
        // TODO: 추가예정
        // boolean hasSticker,
        // boolean certifiedSpotsCount,
        int spotsCount,
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
    public record BaseballGameResponse(
            Long id,
            Long homeTeam,
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

