package com.manruhomerun.yadan.friend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.friend.dto.FriendResponse;
import com.manruhomerun.yadan.friend.service.FriendService;
import com.manruhomerun.yadan.global.dto.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// TODO Filter 작성 후 401 추가
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/friends")
@Tag(name = "Friend", description = "친구 관리 API")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    @Operation(summary = "친구 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<FriendResponse>> getFriends(HttpServletRequest httpRequest) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "친구 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "친구 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "친구 관계를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteFriend(
            @PathVariable Long friendId,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        friendService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
