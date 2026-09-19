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

        @Schema(description = "여행 지역 코드", example = "26000")
        String regionCode,

        @Schema(description = "함께 가는 친구의 사용자 UUID 목록", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
        List<String> friends,

        @Schema(description = "여행 테마 ID", example = "2")
        Long theme,

        @Schema(description = "일차별 여행지 일정")
        List<ScheduleRequest> schedule
) {
    public record BaseballGameRequest(
            @Schema(description = "경기 ID", example = "123")
            Long id,

            @Schema(description = "경기 직전에 배치되는 여행지의 인덱스. 경기 전 여행지가 없으면 -1", example = "2")
            Integer baseballGameAfterIdx
    ) {
    }

    public record ScheduleRequest(
            @Schema(description = "여행 일차", example = "1")
            Integer day,

            @Schema(description = "해당 일차의 여행지 ID 목록", example = "[\"239764\", \"233464\", \"232264\"]")
            List<String> travelSpotIdList
    ) {
    }
}
