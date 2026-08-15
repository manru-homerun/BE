package com.manruhomerun.yadan.travelspot.controller;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.global.dto.PageResponse;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelRegionCode;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDetailResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDibsItemResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotSearchItemResponse;
import com.manruhomerun.yadan.travelspot.service.TravelSpotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/travel/spots")
@Tag(name = "TravelSpot", description = "여행지 API")
public class TravelSpotController {

    private final TravelSpotService travelSpotService;

    @GetMapping
    @Operation(summary = "여행지 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행지 검색 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "502", description = "외부 여행지 API 호출 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<TravelSpotSearchItemResponse>> getSpots(
            @Parameter(description = "검색어", example = "시장", required = true)
            @RequestParam @NotBlank(message = "keyword는 필수입니다.") String keyword,
            @Parameter(description = "조회할 지역", example = "BUSAN", required = true)
            @RequestParam TravelRegionCode region,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "pageNumber는 1 이상이어야 합니다.") int pageNumber,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize는 1 이상이어야 합니다.") int pageSize
    ) {
        return ResponseEntity.ok(travelSpotService.getSpots(keyword, region, pageNumber, pageSize));
    }

    @GetMapping("/{contentId}")
    @Operation(summary = "여행지 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행지 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "여행지를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "외부 여행지 API 호출 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TravelSpotDetailResponse> getSpotDetail(
            @Parameter(description = "외부 관광 API의 contentId", example = "2479634")
            @PathVariable String contentId
    ) {
        //        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(travelSpotService.getSpotDetail(contentId, userId));
    }

    @GetMapping("/{contentId}/images")
    @Operation(summary = "여행지 이미지 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행지 이미지 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "여행지를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "외부 여행지 API 호출 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<String>> getSpotImages(
            @Parameter(description = "외부 관광 API의 contentId", example = "2479634")
            @PathVariable String contentId
    ) {
        return ResponseEntity.ok(travelSpotService.getSpotImages(contentId));
    }

    @PostMapping("/{contentId}/dibs")
    @Operation(summary = "여행지 찜 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "여행지 찜 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
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
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        travelSpotService.createDibs(userId, contentId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{contentId}/dibs")
    @Operation(summary = "여행지 찜 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "여행지 찜 취소 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteDibs(
            @Parameter(description = "외부 관광 API의 contentId", example = "132159")
            @PathVariable String contentId,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        travelSpotService.deleteDibs(userId, contentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dibs")
    @Operation(summary = "여행지 찜 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행지 찜 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 region 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TravelSpotDibsItemResponse>> getDibs(
            @Parameter(description = "조회할 지역", example = "BUSAN")
            @RequestParam TravelRegionCode region,
            HttpServletRequest httpRequest
    ) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(travelSpotService.getDibs(userId, region));
    }
}
