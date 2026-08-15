package com.manruhomerun.yadan.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

// 친구 요청 DTO
public record FriendRequestCreateRequest(
        @NotBlank(message = "친구 요청을 받을 사용자 ID는 필수입니다.")
        @Schema(
                description = "친구 요청을 받을 사용자 ID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        String receiverUserId
) {
}
