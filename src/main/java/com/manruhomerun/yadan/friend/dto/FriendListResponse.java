package com.manruhomerun.yadan.friend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "친구 목록 응답")
public record FriendListResponse(
        @Schema(description = "전체 친구 수", example = "4")
        long friendCount,

        @Schema(description = "받은 대기 친구 요청 수", example = "2")
        long receivedRequestCount,

        @Schema(description = "친구 목록")
        List<FriendResponse> friends
) {
    public static FriendListResponse from(
            List<FriendResponse> friends,
            long receivedRequestCount
    ) {
        return new FriendListResponse(
                friends.size(),
                receivedRequestCount,
                friends
        );
    }
}
