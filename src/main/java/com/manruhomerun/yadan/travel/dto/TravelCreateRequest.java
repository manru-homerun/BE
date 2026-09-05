package com.manruhomerun.yadan.travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "여행 생성 요청")
public record TravelCreateRequest(
        @Schema(description = "여행 시작일", example = "2026-07-03")
        LocalDate from,

        @Schema(description = "여행 종료일", example = "2026-07-05")
        LocalDate to,

        @Schema(description = "직관 경기 정보")
        BaseballGameRequest baseballGame,

        @Schema(description = "여행 이름", example = "부산 사직 직관 여행")
        String name,

        @Schema(description = "지역 코드", example = "41")
        String regionCode,

        @Schema(description = "함께 가는 친구 ID 목록")
        List<String> friends,

        @Schema(description = "여행 테마 ID 목록")
        List<Long> theme,

        @Schema(description = "일차별 여행지 일정")
        List<ScheduleRequest> schedule
) {
    public record BaseballGameRequest(
            @Schema(description = "경기 ID", example = "123")
            Long id,

            @Schema(description = "당일 경기 이후 일정 시작 인덱스", example = "3")
            Integer baseballGameAfterIdx
    ) {
    }

    public record ScheduleRequest(
            @Schema(description = "여행 일차", example = "1")
            Integer day,

            @Schema(description = "해당 일차의 여행지 ID 목록")
            List<String> travelSpotIdList
    ) {
    }
}
