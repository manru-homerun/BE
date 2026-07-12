package com.manruhomerun.yadan.travelspot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDibsItemResponse;
import com.manruhomerun.yadan.travelspot.service.TravelSpotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel/spots")
@Tag(name = "TravelSpot", description = "여행지 API")
public class TravelSpotController {

    private final TravelSpotService travelSpotService;

    @PostMapping("/{contentId}/dibs")
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
            @Parameter(description = "외부 관광 API의 contentId", example = "132159")
            @PathVariable String contentId,
            HttpServletRequest httpRequest
    ) {
        String userId = (String) httpRequest.getAttribute("userId");

        travelSpotService.createDibs(userId, contentId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{contentId}/dibs")
    @Operation(summary = "여행지 찜 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "여행지 찜 취소 성공"),
            @ApiResponse(responseCode = "401", description = "사용자 식별값 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteDibs(
            @Parameter(description = "외부 관광 API의 contentId", example = "132159")
            @PathVariable String contentId,
            HttpServletRequest httpRequest
    ) {
        String userId = (String) httpRequest.getAttribute("userId");

        travelSpotService.deleteDibs(userId, contentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dibs")
    @Operation(summary = "여행지 찜 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행지 찜 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 regionCode 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "사용자 식별값 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TravelSpotDibsItemResponse>> getDibs(
            @Parameter(description = "조회할 지역 코드", example = "26000")
            @RequestParam String regionCode,
            HttpServletRequest httpRequest
    ) {
        String userId = (String) httpRequest.getAttribute("userId");

        return ResponseEntity.ok(travelSpotService.getDibs(userId, regionCode));
    }
}
