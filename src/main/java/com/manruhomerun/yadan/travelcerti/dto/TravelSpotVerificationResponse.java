package com.manruhomerun.yadan.travelcerti.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TravelSpotVerificationResponse(
        @Schema(description = "현재 사용자가 해당 여행에서 인증한 여행지 수", example = "3")
        long totalVerifiedSpotsCnt
) {
}
