package com.manruhomerun.yadan.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.notification.dto.NotificationResponse;
import com.manruhomerun.yadan.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/notifications")
@Tag(name = "Notification", description = "앱 내 알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<NotificationResponse>> getNotifications(HttpServletRequest httpRequest) {
        // TODO 인증 연동 시 request attribute에서 userId 조회
        // String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111";

        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }
}
