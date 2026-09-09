package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "여행 코스 정렬 응답")
public record TravelAlignResponse(
        @Schema(description = "직관 경기 정보")
        BaseballGameResponse baseballGame,

        @Schema(description = "일차별 여행 일정")
        List<ScheduleResponse> schedule
) {
    @Schema(description = "직관 경기 응답 정보")
    public record BaseballGameResponse(
            @Schema(description = "경기 ID", example = "123")
            Long id,

            @Schema(description = "경기 관람 일차", example = "2")
            Integer day,

            @Schema(description = "경기 직전에 배치되는 여행지의 인덱스. 경기 전 여행지가 없으면 -1", example = "2")
            Integer baseballGameAfterIdx
    ) {
    }

    @Schema(description = "일차별 여행 일정")
    public record ScheduleResponse(
            @Schema(description = "여행 일차", example = "1")
            Integer day,

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
