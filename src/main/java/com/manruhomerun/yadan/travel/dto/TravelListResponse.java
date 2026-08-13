package com.manruhomerun.yadan.travel.dto;

import com.manruhomerun.yadan.travel.domain.entity.Travel;

import java.time.LocalDate;

public record TravelListResponse(
        LocalDate from,
        LocalDate to,
        String name,
        String regionCode,
        // boolean hasSticker,
        int spotsCount
) {
    public static TravelListResponse from(Travel travel) {
        return new TravelListResponse(
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getName(),
                travel.getRegionCode(),
                // travel.getTravelTravelSpotList() != null && !travel.getTravelTravelSpotList().isEmpty(),
                travel.getTravelTravelSpotList() != null ? travel.getTravelTravelSpotList().size() : 0
        );
    }
}
