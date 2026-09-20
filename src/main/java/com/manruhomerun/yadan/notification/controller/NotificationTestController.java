package com.manruhomerun.yadan.notification.controller;

import static com.manruhomerun.yadan.global.config.SwaggerConfig.BEARER_AUTH;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.notification.dto.NotificationTestPushRequest;
import com.manruhomerun.yadan.notification.dto.NotificationTestPushResponse;
import com.manruhomerun.yadan.notification.service.NotificationTestPushService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/notifications/test-push")
@Tag(name = "Notification Test", description = "FCM 연결 및 전송 테스트 API")
@SecurityRequirement(name = BEARER_AUTH)
@ConditionalOnProperty(
        name = {"firebase.enabled", "notification.test-api-enabled"},
        havingValue = "true"
)
public class NotificationTestController {

    private final NotificationTestPushService notificationTestPushService;

    @PostMapping
    @Operation(
            summary = "FCM 테스트 푸시 전송",
            description = "현재 사용자의 등록된 FID를 대상으로 FCM 검증 또는 실제 전송을 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 테스트 푸시 처리 완료",
                    content = @Content(schema = @Schema(implementation = NotificationTestPushResponse.class))),
            @ApiResponse(responseCode = "400", description = "테스트 푸시 요청값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 등록된 앱 설치 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<NotificationTestPushResponse> send(
            @Valid @RequestBody NotificationTestPushRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = (String) httpRequest.getAttribute("userId");

        return ResponseEntity.ok(notificationTestPushService.send(userId, request));
    }
}
