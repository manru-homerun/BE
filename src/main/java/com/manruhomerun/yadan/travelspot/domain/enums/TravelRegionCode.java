package com.manruhomerun.yadan.travelspot.domain.enums;

public enum TravelRegionCode {
    GWANGJU("12000", "광주"),
    SEOUL("11000", "서울"),
    DAEJEON("30000", "대전"),
    SUWON("41110", "수원"),
    DAEGU("27000", "대구"),
    INCHEON("28000", "인천"),
    BUSAN("26000", "부산"),
    CHANGWON("48120", "창원");

    private final String code;
    private final String cityName;

    TravelRegionCode(String code, String cityName) {
        this.code = code;
        this.cityName = cityName;
    }

    public String getCodePrefix() {
        return code.replaceFirst("0+$", "");
    }
}
