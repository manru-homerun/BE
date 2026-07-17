package com.manruhomerun.yadan.travelspot.domain.enums;

import lombok.Getter;

public enum TravelSpotCategory {
    TOURIST_ATTRACTION(12, "관광지"),
    CULTURAL_FACILITY(14, "문화시설"),
    FESTIVAL_PERFORMANCE_EVENT(15, "축제공연행사"),
    TRAVEL_COURSE(25, "여행코스"),
    LEPORTS(28, "레포츠"),
    ACCOMMODATION(32, "숙박"),
    SHOPPING(38, "쇼핑"),
    RESTAURANT(39, "음식점");

    private final Integer contentTypeId;
    @Getter
    private final String displayName;

    TravelSpotCategory(Integer contentTypeId, String displayName) {
        this.contentTypeId = contentTypeId;
        this.displayName = displayName;
    }

    public static TravelSpotCategory fromContentTypeId(Integer contentTypeId) {
        for (TravelSpotCategory category : values()) {
            if (category.contentTypeId.equals(contentTypeId)) {
                return category;
            }
        }

        return null;
    }

    public static String getDisplayNameByContentTypeId(Integer contentTypeId) {
        TravelSpotCategory category = fromContentTypeId(contentTypeId);

        if (category == null) {
            return "기타";
        }

        return category.getDisplayName();
    }
}
