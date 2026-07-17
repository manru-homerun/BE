package com.manruhomerun.yadan.baseball.domain.enums;

import java.util.Arrays;

public enum KboTeamCode {
    // 프로야구 팀 PK 하드코딩 -> 어차피 자주 변경되지 않는 데이터인데, 굳이 매번 DB에서 조회할 필요가 없다고 판단
    KIA(1L, "KIA 타이거즈"),
    LG(2L, "LG 트윈스"),
    DOOSAN(3L, "두산 베어스"),
    HANWHA(4L, "한화 이글스"),
    KT(5L, "KT 위즈"),
    KIWOOM(6L, "키움 히어로즈"),
    SAMSUNG(7L, "삼성 라이온즈"),
    SSG(8L, "SSG 랜더스"),
    LOTTE(9L, "롯데 자이언츠"),
    NC(10L, "NC 다이노스");

    private final Long teamId;
    private final String teamName;

    KboTeamCode(Long teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public static KboTeamCode from(String value) {
        return Arrays.stream(values())
                .filter(teamCode -> teamCode.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
