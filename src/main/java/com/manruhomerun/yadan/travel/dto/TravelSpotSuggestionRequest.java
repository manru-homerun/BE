package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.travel.domain.enums.CompanionCondition;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "여행지 추천 요청")
public record TravelSpotSuggestionRequest(
        @Schema(description = "여행 시작일", example = "2026-07-30")
        LocalDate from,

        @Schema(description = "여행 종료일", example = "2026-08-01")
        LocalDate to,

        @Schema(description = "여행 지역 코드", example = "11000")
        String regionCode,

        @Schema(description = "동행 조건 목록")
        List<CompanionCondition> companionConditions,

        @Schema(description = "본인을 제외한 동반 인원 수", example = "1")
        int companionCount,

        @Schema(description = "여행 테마 ID", example = "2")
        Long theme,

        @Schema(description = "기존 여행지 ID 목록", example = "[129854, 294505]")
        List<Long> travelSpotIdList
) {
}
