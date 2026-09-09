package com.manruhomerun.yadan.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.user.dto.NicknameCheckRequest;
import com.manruhomerun.yadan.user.dto.NicknameCheckResponse;
import com.manruhomerun.yadan.user.dto.OnboardingRequest;
import com.manruhomerun.yadan.user.dto.TravelPreferenceResponse;
import com.manruhomerun.yadan.user.dto.TravelPreferenceUpdateRequest;
import com.manruhomerun.yadan.user.dto.UserProfileResponse;
import com.manruhomerun.yadan.user.dto.UserProfileUpdateRequest;
import com.manruhomerun.yadan.user.dto.UserSearchRequest;
import com.manruhomerun.yadan.user.dto.UserSearchResponse;
import com.manruhomerun.yadan.user.service.UserService;

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
@RequestMapping("/users")
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/onboarding")
    @Operation(summary = "온보딩 정보 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "온보딩 정보 저장 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 잘못되었거나 필수 약관에 동의하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 응원 구단을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 온보딩을 완료함",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> onboard(
            @Valid @RequestBody OnboardingRequest request,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 인증 연동 전 임시 사용자 ID
        userService.onboard(userId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nickname/check")
    @Operation(summary = "닉네임 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 확인 성공",
                    content = @Content(schema = @Schema(implementation = NicknameCheckResponse.class))),
            @ApiResponse(responseCode = "400", description = "닉네임 형식이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<NicknameCheckResponse> checkNickname(
            @Valid @ModelAttribute NicknameCheckRequest request,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 인증 연동 전 임시 사용자 ID

        return ResponseEntity.ok(userService.checkNickname(userId, request));
    }

    @GetMapping
    @Operation(summary = "사용자 닉네임 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 검색 성공",
                    content = @Content(schema = @Schema(implementation = UserSearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "검색 조건이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "현재 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserSearchResponse> searchUsers(
            @Valid @ModelAttribute UserSearchRequest request,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 인증 연동 전 임시 사용자 ID

        return ResponseEntity.ok(userService.searchUsers(userId, request));
    }

    @GetMapping("/me/profile")
    @Operation(summary = "나의 프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserProfileResponse> getProfile(HttpServletRequest httpRequest) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 인증 연동 전 임시 사용자 ID

        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @GetMapping("/me/preference")
    @Operation(summary = "나의 여행 취향 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 취향 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = TravelPreferenceResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 여행 취향 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TravelPreferenceResponse> getPreference(HttpServletRequest httpRequest) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 인증 연동 전 임시 사용자 ID

        return ResponseEntity.ok(userService.getPreference(userId));
    }

    @PutMapping("/me/preference")
    @Operation(summary = "나의 여행 취향 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 취향 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = TravelPreferenceResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 또는 여행 지역이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 여행 취향 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TravelPreferenceResponse> updatePreference(
            @Valid @RequestBody TravelPreferenceUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 인증 연동 전 임시 사용자 ID

        return ResponseEntity.ok(userService.updatePreference(userId, request));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "나의 프로필 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 응원 구단을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 인증 연동 전 임시 사용자 ID

        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }
}
