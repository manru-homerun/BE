package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "인기 여행지 응답")
public record PopularTravelSpotResponse(
        @Schema(description = "인기 여행지 목록")
        List<ContentResponse> contents
) {
    @Schema(description = "인기 여행지 정보")
    public record ContentResponse(
            @Schema(description = "여행지 ID", example = "132159")
            String id,

            @Schema(description = "여행지 주소", example = "제주특별자치도 서귀포시 성산읍 고성오조로 93")
            String address,

            @Schema(description = "여행지 카테고리", example = "쇼핑")
            String category,

            @Schema(description = "여행지 대표 이미지", example = "https://example.com/travel-spot.jpg", nullable = true)
            String image,

            @Schema(description = "여행지 이름", example = "고성오일시장")
            String title,

            @Schema(description = "여행지 지역 코드", example = "33200")
            Integer regionCode
    ) {
        public static ContentResponse from(TravelSpot travelSpot, String address) {
            return new ContentResponse(
                    travelSpot.getId(),
                    address,
                    TravelSpotCategory.getDisplayNameByContentTypeId(travelSpot.getCategory()),
                    travelSpot.getImage(),
                    travelSpot.getName(),
                    Integer.valueOf(travelSpot.getRegionCode())
            );
        }
    }
}
