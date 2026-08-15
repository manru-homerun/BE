package com.manruhomerun.yadan.travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TravelModifyRequest(
        String name,
        Integer gameIdx,
        List<ScheduleRequest> schedule

) {
    public record ScheduleRequest(
            @Schema(description = "여행 일차", example = "1")
            Integer day,

            @Schema(description = "해당 일차의 여행지 ID 목록")
            List<String> travelSpotIdList
    ) {
    }
}
