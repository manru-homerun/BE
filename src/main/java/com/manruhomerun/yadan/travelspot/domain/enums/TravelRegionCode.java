package com.manruhomerun.yadan.travelspot.domain.enums;

// 프론트로부터 지역 코드 값(숫자 형식)을 받는 게 맞는지는 고민해볼 주제인 것 같습니다.
// 일단은 지역 코드 받도록 구현해두겠습니다.
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

    public static boolean isSupported(String code) {
        for (TravelRegionCode regionCode : values()) {
            if (regionCode.code.equals(code)) {
                return true;
            }
        }

        return false;
    }
}
