package com.manruhomerun.yadan.travelspot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDibsRequest;
import com.manruhomerun.yadan.travelspot.service.TravelSpotService;

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
@RequestMapping("/travel/spots")
@Tag(name = "TravelSpot", description = "여행지 API")
public class TravelSpotController {

    private final TravelSpotService travelSpotService;

    @PostMapping("/dibs")
    @Operation(summary = "여행지 찜 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "여행지 찜 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "사용자 식별값 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 여행지를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "외부 여행지 API 호출 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> createDibs(
            @Valid @RequestBody TravelSpotDibsRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = (String) httpRequest.getAttribute("userId");

        travelSpotService.createDibs(userId, request.contentId());
        return ResponseEntity.status(201).build();
    }
}
