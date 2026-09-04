package com.manruhomerun.yadan.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.notification.dto.NotificationSettingResponse;
import com.manruhomerun.yadan.notification.dto.NotificationSettingUpdateRequest;
import com.manruhomerun.yadan.notification.service.NotificationSettingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/notification-settings")
@Tag(name = "Notification Setting", description = "알림 설정 API")
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping
    @Operation(summary = "알림 설정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 설정 조회 성공",
                    content = @Content(schema = @Schema(implementation = NotificationSettingResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 알림 설정을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<NotificationSettingResponse> getSettings(HttpServletRequest request) {
        // TODO 인증 연동 시 request attribute에서 userId 조회
        // String userId = (String) request.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111";

        return ResponseEntity.ok(notificationSettingService.getSettings(userId));
    }

    @PutMapping
    @Operation(summary = "알림 설정 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "알림 설정 수정 성공"),
            @ApiResponse(responseCode = "400", description = "알림 설정값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 알림 설정을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updateSettings(
            @Valid @RequestBody NotificationSettingUpdateRequest updateRequest,
            HttpServletRequest httpRequest
    ) {
        // TODO 인증 연동 시 request attribute에서 userId 조회
        // String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111";

        notificationSettingService.updateSettings(userId, updateRequest);
        return ResponseEntity.noContent().build();
    }
}
