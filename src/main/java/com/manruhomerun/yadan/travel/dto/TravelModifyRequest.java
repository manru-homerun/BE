package com.manruhomerun.yadan.travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "여행 수정 요청")
public record TravelModifyRequest(
        @Schema(description = "여행 이름", example = "엘지트윈스짱")
        String name,

        @Schema(description = "경기가 관람일자 일정의 어느 인덱스 뒤에 위치하는지 표현. -1일 경우 맨 앞", example = "2")
        Integer gameIdx,

        @Schema(description = "일차별 여행지 일정")
        List<ScheduleRequest> schedule

) {
    public record ScheduleRequest(
            @Schema(description = "여행 일차", example = "1")
            Integer day,

            @Schema(description = "해당 일차의 여행지 ID 목록", example = "[\"2871024\", \"129854\", \"4012773\"]")
            List<String> travelSpotIdList
    ) {
    }
}
