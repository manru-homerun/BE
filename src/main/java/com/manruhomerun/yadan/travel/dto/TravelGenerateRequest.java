package com.manruhomerun.yadan.travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "여행 코스 생성 요청")
public record TravelGenerateRequest(
        @Schema(description = "여행 시작일", example = "2026-07-03")
        LocalDate from,

        @Schema(description = "여행 종료일", example = "2026-07-05")
        LocalDate to,

        @Schema(description = "직관 경기 ID", example = "123")
        Long baseballGameId,

        @Schema(description = "여행 지역 코드", example = "22")
        String regionCode,

        @Schema(description = "무장애 여행지 우선 여부", example = "true")
        boolean barrierFree,

        @Schema(description = "함께 가는 친구 ID 목록", example = "[\"jamy\", \"lida\"]")
        List<String> friends,

        @Schema(description = "여행 테마 ID 목록", example = "[2, 3]")
        List<Long> theme,

        @Schema(description = "여행지 ID 목록", example = "[239764, 233464, 232264]")
        List<Long> travelSpotIdList
) {
}
