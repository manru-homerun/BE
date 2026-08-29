package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.travel.domain.entity.Theme;
import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.travel.domain.entity.TravelTheme;
import com.manruhomerun.yadan.travel.domain.entity.TravelTravelSpot;
import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Schema(description = "여행 상세 응답")
public record TravelDetailResponse(
        @Schema(description = "여행 시작일", example = "2026-08-15")
        LocalDate from,

        @Schema(description = "여행 종료일", example = "2026-08-16")
        LocalDate to,

        @Schema(description = "직관 경기 정보")
        BaseballGameResponse baseballGame,

        @Schema(description = "여행 이름", example = "엘지트윈스짱")
        String name,

        @Schema(description = "지역 코드", example = "11000")
        String regionCode,

        @Schema(description = "함께 가는 친구 목록")
        List<String> friends,

        @Schema(description = "현재 사용자의 방장 여부", example = "true")
        boolean isLeader,

        @Schema(description = "여행 테마 목록")
        List<String> theme,

        @Schema(description = "일차별 여행 일정")
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

        List<String> theme = safeTravelThemes.stream()
                .map(TravelTheme::getTheme)
                .filter(Objects::nonNull)
                .map(Theme::getName)
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

    @Schema(description = "직관 경기 상세 정보")
    public record BaseballGameResponse(
            @Schema(description = "경기 ID", example = "1091")
            Long id,

            @Schema(description = "여행 기준 경기 일차", example = "1")
            Integer day,

            @Schema(description = "경기가 관람일자 일정의 어느 인덱스 뒤에 위치하는지 표현. -1일 경우 맨 앞", example = "2")
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

    @Schema(description = "일차별 여행 일정")
    public record ScheduleResponse(
            @Schema(description = "여행 일차", example = "1")
            int day,

            @Schema(description = "해당 일차의 여행지 목록")
            List<TravelSpotResponse> travelSpotList
    ) {
    }

    @Schema(description = "여행지 요약 정보")
    public record TravelSpotResponse(
            @Schema(description = "여행지 ID", example = "2871024")
            String id,

            @Schema(description = "여행지 이름", example = "경복궁")
            String name,

            @Schema(description = "여행지 카테고리", example = "관광지")
            String category,

            @Schema(description = "여행지 대표 이미지", example = "https://example.com/travel-spot.jpg", nullable = true)
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
