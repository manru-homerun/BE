package com.manruhomerun.yadan.baseball.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum BaseballErrorCode implements BaseErrorCode {
    BASEBALL_GAME_NOT_FOUND("BASEBALL_GAME_404", "경기를 찾을 수 없습니다.", 404),
    BASEBALL_STADIUM_NOT_FOUND("BASEBALL_STADIUM_404", "구장을 찾을 수 없습니다.", 404),
    BASEBALL_TEAM_NOT_FOUND("BASEBALL_TEAM_404", "팀을 찾을 수 없습니다.", 404);

    private final String code;
    private final String defaultMessage;
    private final int status;

    BaseballErrorCode(String code, String defaultMessage, int status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
