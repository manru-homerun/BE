package com.manruhomerun.yadan.travelspot.domain.enums;

public enum PreferredTravelRegionCode {

    SEOUL("11000", "서울"),
    BUSAN("26000", "부산"),
    DAEGU("27000", "대구"),
    INCHEON("28000", "인천"),
    GWANGJU("29000", "광주"),
    DAEJEON("30000", "대전"),
    ULSAN("31000", "울산"),
    SEJONG("36000", "세종"),
    GYEONGGI("41000", "경기"),
    GANGWON("42000", "강원"),
    CHUNGBUK("43000", "충북"),
    CHUNGNAM("44000", "충남"),
    JEONBUK("45000", "전북"),
    JEONNAM("46000", "전남"),
    GYEONGBUK("47000", "경북"),
    GYEONGNAM("48000", "경남"),
    JEJU("50000", "제주");

    private final String code;
    private final String regionName;

    PreferredTravelRegionCode(String code, String regionName) {
        this.code = code;
        this.regionName = regionName;
    }

    public String getCode() {
        return code;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getCodePrefix() {
        return code.replaceFirst("0+$", "");
    }

    public static PreferredTravelRegionCode fromCode(String code) {
        for (PreferredTravelRegionCode regionCode : values()) {
            if (regionCode.code.equals(code)) {
                return regionCode;
            }
        }

        throw new IllegalArgumentException(
                "지원하지 않는 선호 여행 지역 코드입니다: " + code
        );
    }

    public static PreferredTravelRegionCode fromRegionName(String regionName) {
        for (PreferredTravelRegionCode regionCode : values()) {
            if (regionCode.regionName.equals(regionName)) {
                return regionCode;
            }
        }

        throw new IllegalArgumentException(
                "지원하지 않는 선호 여행 지역 이름입니다: " + regionName
        );
    }
}