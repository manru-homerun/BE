package com.manruhomerun.yadan.travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "여행 코스 정렬 요청")
public record TravelAlignRequest(
        @Schema(description = "여행 시작일", example = "2026-07-03")
        LocalDate from,

        @Schema(description = "여행 종료일", example = "2026-07-05")
        LocalDate to,

        @Schema(description = "직관 경기 정보")
        BaseballGameRequest baseballGame,

        @Schema(description = "일차별 여행지 일정")
        List<ScheduleRequest> schedule
) {
    @Schema(description = "직관 경기 요청 정보")
    public record BaseballGameRequest(
            @Schema(description = "경기 ID", example = "123")
            Long id
    ) {
    }

    @Schema(description = "일차별 여행지 일정")
    public record ScheduleRequest(
            @Schema(description = "여행 일차", example = "1")
            Integer day,

            @Schema(description = "해당 일차의 여행지 ID 목록", example = "[\"239764\", \"233464\", \"232264\"]")
            List<String> travelSpotIdList
    ) {
    }
}
