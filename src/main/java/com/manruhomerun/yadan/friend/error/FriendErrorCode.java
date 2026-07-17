package com.manruhomerun.yadan.friend.error;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

public enum FriendErrorCode implements BaseErrorCode {
    SELF_REQUEST_NOT_ALLOWED("FRIEND_400_SELF_REQUEST", "자기 자신에게 친구 요청을 보낼 수 없습니다.", 400),
    REQUEST_NOT_FOUND("FRIEND_404_REQUEST", "친구 요청을 찾을 수 없습니다.", 404),
    REQUEST_ALREADY_EXISTS("FRIEND_409_REQUEST_EXISTS", "대기 중인 친구 요청이 이미 존재합니다.", 409),
    ALREADY_FRIENDS("FRIEND_409_ALREADY_FRIENDS", "이미 친구인 사용자입니다.", 409),
    REQUEST_ALREADY_PROCESSED("FRIEND_409_REQUEST_PROCESSED", "이미 처리된 친구 요청입니다.", 409);

    private final String code;
    private final String defaultMessage;
    private final int status;

    FriendErrorCode(String code, String defaultMessage, int status) {
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
