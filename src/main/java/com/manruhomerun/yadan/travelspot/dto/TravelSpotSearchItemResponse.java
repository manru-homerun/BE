package com.manruhomerun.yadan.travelspot.dto;

import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record TravelSpotSearchItemResponse(
        @Schema(description = "여행지 ID", example = "132159")
        String id,
        @Schema(description = "주소", example = "제주특별자치도 서귀포시 성산읍 고성오조로 93")
        String address,
        @Schema(description = "여행지 카테고리", example = "쇼핑")
        String category,
        @Schema(description = "대표 이미지", example = "http://tong.visitkorea.or.kr/cms/resource/92/2947292_image2_1.jpg", nullable = true)
        String image,
        @Schema(description = "여행지 이름", example = "고성오일시장")
        String title,
        @Schema(description = "법정동 지역 코드", example = "50332")
        String regionCode
) {

    public static TravelSpotSearchItemResponse from(TourApiSearchKeywordResponse.Item item) {
        String address = item.addr2() == null || item.addr2().isBlank()
                ? item.addr1()
                : item.addr1() + " " + item.addr2();

        return new TravelSpotSearchItemResponse(
                item.contentid(),
                address,
                TravelSpotCategory.getDisplayNameByContentTypeId(Integer.valueOf(item.contenttypeid())),
                item.firstimage(),
                item.title(),
                item.lDongRegnCd() + item.lDongSignguCd()
        );
    }
}
