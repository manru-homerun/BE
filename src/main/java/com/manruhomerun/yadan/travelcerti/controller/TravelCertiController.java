package com.manruhomerun.yadan.travelcerti.controller;

import com.manruhomerun.yadan.travelcerti.dto.TravelSpotVerificationRequest;
import com.manruhomerun.yadan.travelcerti.dto.TravelSpotVerificationResponse;
import com.manruhomerun.yadan.travelcerti.service.TravelCertiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
@Tag(name = "Travel Certification", description = "여행지 방문 인증 API")
public class TravelCertiController {

    private final TravelCertiService travelCertiService;

    @PostMapping("/{travelId}/spots/{spotId}/verification")
    @Operation(summary = "여행지 방문 인증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행지 방문 인증 성공")
    })
    public ResponseEntity<TravelSpotVerificationResponse> verifyTravelSpot(
            @Parameter(description = "여행 ID", example = "1e3a5081-675e-4264-8e56-ebb659e12acd")
            @PathVariable String travelId,
            @Parameter(description = "여행지 ID", example = "132159")
            @PathVariable String spotId,
            @Valid @RequestBody TravelSpotVerificationRequest request,
            HttpServletRequest httpRequest
    ) {
        //        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(travelCertiService.verifyTravelSpot(userId, travelId, spotId, request));
    }
}
