package com.manruhomerun.yadan.user.dto;

import com.manruhomerun.yadan.travelspot.domain.enums.PreferredTravelRegionCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 지역 정보")
public record TravelRegionResponse(

        @Schema(description = "여행 지역 코드", example = "11000")
        String regionCode,

        @Schema(description = "여행 지역 이름", example = "서울")
        String regionName

) {

    public static TravelRegionResponse from(PreferredTravelRegionCode regionCode) {
        return new TravelRegionResponse(
                regionCode.getCode(),
                regionCode.getRegionName()
        );
    }
}
