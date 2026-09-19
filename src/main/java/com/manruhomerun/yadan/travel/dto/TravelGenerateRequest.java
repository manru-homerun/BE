package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.travel.domain.enums.CompanionCondition;
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

        @Schema(description = "여행 지역 법정동 코드", example = "26000")
        String regionCode,

        @Schema(description = "동행 조건 목록", example = "[\"CHILD\", \"ELDERLY\", \"WHEELCHAIR\"]")
        List<CompanionCondition> companionConditions,

        @Schema(description = "함께 가는 친구의 사용자 UUID 목록", example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"550e8400-e29b-41d4-a716-446655440001\"]")
        List<String> friends,

        @Schema(description = "여행 테마 ID", example = "2")
        Long theme,

        @Schema(description = "여행지 ID 목록", example = "[\"239764\", \"233464\", \"232264\"]")
        List<String> travelSpotIdList
) {
}
