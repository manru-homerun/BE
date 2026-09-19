package com.manruhomerun.yadan.travel.dto;

import java.util.List;

public record AiTravelSpotRecommendRequest(
        String ageGroup,
        String areaCode,
        int companionCount,
        List<String> contentIdSequence,
        String gender,
        int hasChild,
        int hasDisabled,
        int hasElderly,
        List<String> preferredArea,
        String residenceArea,
        String travelDuration,
        Long travelPersona,
        String travelerStyle
) {
}
