package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.travel.domain.entity.Theme;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 테마 응답")
public record ThemeListResponse(
        @Schema(description = "테마 ID", example = "1")
        Long id,

        @Schema(description = "테마명", example = "맛집 탐방")
        String name
) {
    public static ThemeListResponse from(Theme theme) {
        return new ThemeListResponse(theme.getId(), theme.getName());
    }
}
