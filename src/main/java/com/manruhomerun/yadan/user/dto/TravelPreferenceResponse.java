package com.manruhomerun.yadan.user.dto;

import java.util.Comparator;
import java.util.List;

import com.manruhomerun.yadan.travelspot.domain.enums.PreferredTravelRegionCode;
import com.manruhomerun.yadan.user.domain.entity.TravelPreference;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "나의 여행 취향 정보 조회 응답")
public record TravelPreferenceResponse(

        @Schema(description = "여행 스타일 값, 자연 선호 1부터 도시 선호 7까지", example = "4")
        Integer travelStyleValue,

        @Schema(description = "거주 지역 정보")
        TravelRegionResponse residenceRegion,

        @Schema(description = "선호 여행 지역 목록")
        List<TravelRegionResponse> preferredRegions

) {

    public static TravelPreferenceResponse from(TravelPreference travelPreference) {
        List<TravelRegionResponse> preferredRegions = travelPreference.getPreferredRegionCodes()
                .stream()
                .sorted(Comparator.comparing(PreferredTravelRegionCode::getCode))
                .map(TravelRegionResponse::from)
                .toList();

        return new TravelPreferenceResponse(
                travelPreference.getTravelStyleValue(),
                TravelRegionResponse.from(travelPreference.getResidenceRegionCode()),
                preferredRegions
        );
    }
}
