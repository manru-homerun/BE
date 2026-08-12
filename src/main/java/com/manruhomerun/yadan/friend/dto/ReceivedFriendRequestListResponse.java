package com.manruhomerun.yadan.friend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "받은 친구 요청 목록 응답")
public record ReceivedFriendRequestListResponse(
        @Schema(description = "전체 친구 수", example = "4")
        long friendCount,

        @Schema(description = "받은 대기 친구 요청 수", example = "2")
        long receivedRequestCount,

        @Schema(description = "보낸 대기 친구 요청 수", example = "1")
        long sentRequestCount,

        @Schema(description = "받은 친구 요청 목록")
        List<ReceivedFriendRequestResponse> receivedRequests
) {
    public static ReceivedFriendRequestListResponse of(
            long friendCount,
            long sentRequestCount,
            List<ReceivedFriendRequestResponse> receivedRequests
    ) {
        return new ReceivedFriendRequestListResponse(
                friendCount,
                receivedRequests.size(),
                sentRequestCount,
                receivedRequests
        );
    }
}
