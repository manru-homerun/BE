package com.manruhomerun.yadan.friend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "친구 요청 목록 응답")
public record FriendRequestListResponse(
        @Schema(description = "대기 중인 친구 요청 총개수", example = "2")
        long totalCount,

        @Schema(description = "친구 요청 목록")
        List<FriendRequestResponse> requests
) {
    public static FriendRequestListResponse from(List<FriendRequestResponse> requests) {
        return new FriendRequestListResponse(requests.size(), requests);
    }
}
