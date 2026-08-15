package com.manruhomerun.yadan.travelspot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TravelSpotDetailResponse(
        @Schema(description = "여행지 ID", example = "2479634")
        String id,
        @Schema(description = "여행지 카테고리", example = "축제·공연·행사", nullable = true)
        String category,
        @Schema(description = "여행지 제목", example = "2023 제20회 대한민국향토식문화대전(&남북음식문화축제)")
        String title,
        @Schema(description = "전화번호", example = "남용진(02-577-1138, 010-2750-4432)", nullable = true)
        String tel,
        @Schema(description = "홈페이지 HTML", example = "<a href=\"http://www.foodcf.co.kr\" target=\"_blank\" title=\"새창: 대한민국향토식문화대전 홈페이지로 이동\">http://www.foodcf.co.kr</a>", nullable = true)
        String homepage,
        @Schema(description = "법정동 지역 코드", example = "11290")
        String regionCode,
        @Schema(description = "주소", example = "서울특별시 서초구 강남대로 27 AT센터 제1전시장", nullable = true)
        String address,
        @Schema(description = "경도", example = "127.0407514903", nullable = true)
        String longitude,
        @Schema(description = "위도", example = "37.4673918780", nullable = true)
        String latitude,
        @Schema(description = "개요", nullable = true)
        String overview
) {
}
