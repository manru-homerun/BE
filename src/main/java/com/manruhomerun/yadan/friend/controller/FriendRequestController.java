package com.manruhomerun.yadan.friend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.friend.dto.FriendRequestCreateRequest;
import com.manruhomerun.yadan.friend.dto.FriendRequestResponse;
import com.manruhomerun.yadan.friend.dto.ReceivedFriendRequestListResponse;
import com.manruhomerun.yadan.friend.dto.SentFriendRequestListResponse;
import com.manruhomerun.yadan.friend.service.FriendRequestService;
import com.manruhomerun.yadan.global.dto.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// TODO Filter 작성 후 401 추가
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/friend-requests")
@Tag(name = "Friend Request", description = "친구 신청 API")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @PostMapping
    @Operation(summary = "친구 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "친구 요청 전송 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "자기 자신에게 요청하거나 요청값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 친구이거나 대기 중인 요청이 존재함",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FriendRequestResponse> createRequest(
            @Valid @RequestBody FriendRequestCreateRequest request,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        FriendRequestResponse response = friendRequestService.createRequest(userId, request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/received")
    @Operation(summary = "받은 친구 요청 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "받은 친구 요청 목록 조회 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReceivedFriendRequestListResponse> getReceivedRequests(HttpServletRequest httpRequest) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(friendRequestService.getReceivedRequests(userId));
    }

    @GetMapping("/sent")
    @Operation(summary = "보낸 친구 요청 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보낸 친구 요청 목록 조회 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SentFriendRequestListResponse> getSentRequests(HttpServletRequest httpRequest) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(friendRequestService.getSentRequests(userId));
    }

    @PatchMapping("/{requestId}/accept")
    @Operation(summary = "친구 요청 수락")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "친구 요청 수락 성공"),
            @ApiResponse(responseCode = "404", description = "받은 친구 요청을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 처리된 요청이거나 이미 친구임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> acceptRequest(
            @PathVariable Long requestId,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        friendRequestService.acceptRequest(userId, requestId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PatchMapping("/{requestId}/reject")
    @Operation(summary = "친구 요청 거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "친구 요청 거절 성공"),
            @ApiResponse(responseCode = "404", description = "받은 친구 요청을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 처리된 요청임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long requestId,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        friendRequestService.rejectRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{requestId}/cancel")
    @Operation(summary = "친구 요청 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "친구 요청 취소 성공"),
            @ApiResponse(responseCode = "404", description = "보낸 친구 요청을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 처리된 요청임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> cancelRequest(
            @PathVariable Long requestId,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        friendRequestService.cancelRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }
}
