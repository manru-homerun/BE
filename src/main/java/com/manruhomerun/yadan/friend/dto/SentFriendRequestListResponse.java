package com.manruhomerun.yadan.friend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보낸 친구 요청 목록 응답")
public record SentFriendRequestListResponse(
        @Schema(description = "전체 친구 수", example = "4")
        long friendCount,

        @Schema(description = "받은 대기 친구 요청 수", example = "2")
        long receivedRequestCount,

        @Schema(description = "보낸 대기 친구 요청 수", example = "1")
        long sentRequestCount,

        @Schema(description = "보낸 친구 요청 목록")
        List<FriendRequestItemResponse> sentRequests
) {
    public static SentFriendRequestListResponse of(
            long friendCount,
            long receivedRequestCount,
            List<FriendRequestItemResponse> sentRequests
    ) {
        return new SentFriendRequestListResponse(
                friendCount,
                receivedRequestCount,
                sentRequests.size(),
                sentRequests
        );
    }
}
