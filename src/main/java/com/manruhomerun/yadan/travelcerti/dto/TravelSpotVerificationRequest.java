package com.manruhomerun.yadan.travelcerti.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TravelSpotVerificationRequest(
        @Schema(description = "현재 위도", example = "37.512257")
        @NotNull(message = "latitude는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "latitude는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "latitude는 90 이하여야 합니다.")
        BigDecimal latitude,

        @Schema(description = "현재 경도", example = "127.071901")
        @NotNull(message = "longitude는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "longitude는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "longitude는 180 이하여야 합니다.")
        BigDecimal longitude,

        @Schema(description = "GPS 위치 정확도(미터)", example = "18.5")
        @NotNull(message = "accuracy는 필수입니다.")
        @DecimalMin(value = "0.0", message = "accuracy는 0 이상이어야 합니다.")
        BigDecimal accuracy
) {
}
