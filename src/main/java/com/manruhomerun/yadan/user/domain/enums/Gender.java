package com.manruhomerun.yadan.user.domain.enums;

public enum Gender {
    MALE("남"),
    FEMALE("여");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
