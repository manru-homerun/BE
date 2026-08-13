package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.travel.domain.entity.Theme;
import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.travel.domain.entity.TravelTheme;
import com.manruhomerun.yadan.travel.domain.entity.TravelTravelSpot;
import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record TravelDetailResponse(
        LocalDate from,
        LocalDate to,
        BaseballGameResponse baseballGame,
        String name,
        String regionCode,
        List<String> friends,
        boolean isLeader,
        List<Long> theme,
        List<ScheduleResponse> schedule
) {

    public static TravelDetailResponse from(
            Travel travel
    ) {
        List<TravelUser> safeTravelUsers = travel.getTravelUserList() == null ? List.of() : travel.getTravelUserList();
        List<TravelTheme> safeTravelThemes = travel.getTravelThemeList() == null ? List.of() : travel.getTravelThemeList();
        List<TravelTravelSpot> safeTravelTravelSpots = travel.getTravelTravelSpotList() == null ? List.of() : travel.getTravelTravelSpotList();

        List<String> friends = safeTravelUsers.stream()
                .map(TravelUser::getUser)
                .filter(Objects::nonNull)
                .map(user -> user.getNickname() == null ? user.getId() : user.getNickname())
                .toList();

        boolean isLeader = safeTravelUsers.stream()
                .anyMatch(TravelUser::isLeader);

        List<Long> theme = safeTravelThemes.stream()
                .map(TravelTheme::getTheme)
                .filter(Objects::nonNull)
                .map(Theme::getId)
                .toList();

        // day와 order 기준으로 정렬한 뒤 일차별 여행지 목록으로 묶는다.
        List<ScheduleResponse> schedule = safeTravelTravelSpots.stream()
                .sorted(Comparator.comparingInt(TravelTravelSpot::getDay)
                        .thenComparingInt(TravelTravelSpot::getOrder))
                .collect(java.util.stream.Collectors.groupingBy(
                        TravelTravelSpot::getDay,
                        java.util.LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> new ScheduleResponse(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(TravelTravelSpot::getTravelSpot)
                                .filter(Objects::nonNull)
                                .map(TravelSpotResponse::from)
                                .toList()
                ))
                .toList();

        return new TravelDetailResponse(
                travel.getStartDate(),
                travel.getEndDate(),
                BaseballGameResponse.from(travel),
                travel.getName(),
                travel.getRegionCode(),
                friends,
                isLeader,
                theme,
                schedule
        );
    }

    public record BaseballGameResponse(
            Long id,
            Integer day,
            int gameIdx
    ) {
        public static BaseballGameResponse from(Travel travel) {
            BaseballGame baseballGame = travel.getBaseballGame() == null ? null : travel.getBaseballGame();
            int day = (int) ChronoUnit.DAYS.between(travel.getStartDate(), baseballGame.getGameDate().toLocalDate()) + 1;
            Long baseballGameId = baseballGame.getId();
            return new BaseballGameResponse(
                    baseballGameId,
                    day,
                    travel.getGameIdx()
            );
        }
    }

    public record ScheduleResponse(
            int day,
            List<TravelSpotResponse> travelSpotList
    ) {
    }

    public record TravelSpotResponse(
            String id,
            String name,
            String category,
            String image
    ) {
        public static TravelSpotResponse from(TravelSpot travelSpot) {
            return new TravelSpotResponse(
                    travelSpot.getId(),
                    travelSpot.getName(),
                    TravelSpotCategory.getDisplayNameByContentTypeId(travelSpot.getCategory()),
                    travelSpot.getImage()
            );
        }
    }
}
