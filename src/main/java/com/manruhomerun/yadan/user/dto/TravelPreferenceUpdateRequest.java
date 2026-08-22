package com.manruhomerun.yadan.user.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "나의 여행 취향 정보 수정 요청")
public record TravelPreferenceUpdateRequest(

        @Schema(description = "변경할 거주 지역 이름", example = "서울")
        @NotBlank(message = "거주 지역은 필수입니다.")
        String residenceRegion,

        @Schema(description = "여행 스타일 값, 1은 자연 선호이고 7은 도시 선호", example = "5")
        @NotNull(message = "여행 스타일은 필수입니다.")
        @Min(value = 1, message = "여행 스타일 값은 1 이상이어야 합니다.")
        @Max(value = 7, message = "여행 스타일 값은 7 이하여야 합니다.")
        Integer travelStyleValue,

        @Schema(description = "변경할 선호 여행 지역 이름 목록", example = "[\"부산\", \"강원\", \"제주\"]")
        @NotEmpty(message = "선호 여행 지역은 한 개 이상 선택해야 합니다.")
        Set<@NotBlank(message = "선호 여행 지역 이름은 비어 있을 수 없습니다.") String> preferredRegions

) {
}
