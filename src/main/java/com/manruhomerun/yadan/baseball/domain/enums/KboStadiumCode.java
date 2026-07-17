package com.manruhomerun.yadan.baseball.domain.enums;

import java.util.Arrays;

public enum KboStadiumCode {
    // 경기장 PK 하드코딩 -> 어차피 자주 변경되지 않는 데이터인데, 굳이 매번 DB에서 조회할 필요가 없다고 판단
    GWANGJU(1L, "광주-KIA 챔피언스 필드"),
    JAMSIL(2L, "잠실야구장"),
    DAEJEON(3L, "대전 한화생명 볼파크"),
    SUWON(4L, "수원 KT 위즈 파크"),
    GOCHEOKSKY(5L, "고척 스카이돔"),
    DAEGU(6L, "대구삼성라이온즈파크"),
    MUNHAK(7L, "인천 SSG 랜더스필드"),
    SAJIK(8L, "사직야구장"),
    CHANGWON(9L, "창원 NC 파크");

    private final Long stadiumId;
    private final String stadiumName;

    KboStadiumCode(Long stadiumId, String stadiumName) {
        this.stadiumId = stadiumId;
        this.stadiumName = stadiumName;
    }

    public Long getStadiumId() {
        return stadiumId;
    }

    public static KboStadiumCode from(String value) {
        return Arrays.stream(values())
                .filter(stadiumCode -> stadiumCode.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
