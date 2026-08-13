package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.travel.domain.entity.Theme;

public record ThemeListResponse(
        Long id,
        String name
) {
    public static ThemeListResponse from(Theme theme) {
        return new ThemeListResponse(theme.getId(), theme.getName());
    }
}
