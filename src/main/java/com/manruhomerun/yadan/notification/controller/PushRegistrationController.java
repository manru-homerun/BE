package com.manruhomerun.yadan.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.notification.dto.PushRegistrationRequest;
import com.manruhomerun.yadan.notification.service.PushRegistrationService;

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
@RequestMapping("/users/me/push-registrations")
@Tag(name = "Push Registration", description = "FCM 푸시 수신을 위한 앱 설치 정보 등록 API")
public class PushRegistrationController {

    private final PushRegistrationService pushRegistrationService;

    @PostMapping
    @Operation(summary = "앱 설치 정보 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "앱 설치 정보 등록 성공"),
            @ApiResponse(responseCode = "400", description = "앱 설치 정보가 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> register(
            @Valid @RequestBody PushRegistrationRequest registrationRequest,
            HttpServletRequest httpRequest
    ) {
        // TODO 인증 연동 시 request attribute에서 userId 조회
        // String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111";

        pushRegistrationService.register(userId, registrationRequest);
        return ResponseEntity.noContent().build();
    }
}
