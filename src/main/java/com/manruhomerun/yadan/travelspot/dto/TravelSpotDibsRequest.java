package com.manruhomerun.yadan.travelspot.dto;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record TravelSpotDibsRequest(
        @NotBlank(message = "contentId는 필수입니다.")
        @Schema(description = "외부 관광 API의 contentId", example = "132159")
        String contentId
) {
}
