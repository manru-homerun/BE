package com.manruhomerun.yadan.travel.dto;

import java.util.List;

public record AiTravelGenerateRequest(
        String areaCode,
        String travelDuration,
        String travelPersona,
        String ageGroup,
        String gender,
        String travelerStyle,
        List<String> preferredArea,
        String residenceArea,
        boolean hasChild,
        boolean hasElderly,
        boolean hasDisabled,
        int companionCount
) {
}
