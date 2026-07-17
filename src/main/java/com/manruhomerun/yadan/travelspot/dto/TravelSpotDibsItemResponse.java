package com.manruhomerun.yadan.travelspot.dto;

import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record TravelSpotDibsItemResponse(
        @Schema(description = "여행지 ID", example = "2")
        String id,
        @Schema(description = "여행지 이름", example = "해동용궁사")
        String name,
        @Schema(description = "여행지 카테고리", example = "관광지")
        String category,
        @Schema(description = "여행지 대표 이미지", example = "https://example.com/travel-spot.jpg", nullable = true)
        String image
) {

    public static TravelSpotDibsItemResponse from(TravelSpot travelSpot) {
        return new TravelSpotDibsItemResponse(
                travelSpot.getId(),
                travelSpot.getName(),
                TravelSpotCategory.getDisplayNameByContentTypeId(travelSpot.getCategory()),
                travelSpot.getImage()
        );
    }
}
