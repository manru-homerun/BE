package com.manruhomerun.yadan.user.domain.enums;

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

    public String getCode() {
        return code;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCodePrefix() {
        return code.replaceFirst("0+$", "");
    }

    public static TravelRegionCode fromCode(String code) {
        for (TravelRegionCode regionCode : values()) {
            if (regionCode.code.equals(code)) {
                return regionCode;
            }
        }

        throw new IllegalArgumentException("지원하지 않는 지역 코드입니다: " + code);
    }

    public static TravelRegionCode fromCityName(String cityName) {
        for (TravelRegionCode regionCode : values()) {
            if (regionCode.cityName.equals(cityName)) {
                return regionCode;
            }
        }

        throw new IllegalArgumentException("지원하지 않는 지역 이름입니다: " + cityName);
    }
}
