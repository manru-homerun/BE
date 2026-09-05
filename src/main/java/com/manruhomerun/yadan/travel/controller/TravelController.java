package com.manruhomerun.yadan.travel.controller;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.global.dto.PageResponse;
import com.manruhomerun.yadan.travel.domain.enums.TravelStatus;
import com.manruhomerun.yadan.travel.dto.TravelCreateRequest;
import com.manruhomerun.yadan.travel.dto.TravelDetailResponse;
import com.manruhomerun.yadan.travel.dto.TravelListResponse;
import com.manruhomerun.yadan.travel.dto.TravelModifyRequest;
import com.manruhomerun.yadan.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
@Tag(name = "Travel", description = "여행 API")
public class TravelController {
    private final TravelService travelService;

    // 여행 코스 최초 저장
    @PostMapping
    @Operation(summary = "여행 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자, 경기 또는 여행 리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> createTravel(
            @RequestBody TravelCreateRequest request
    ){
        //        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        travelService.createTravel(userId, request);
        return ResponseEntity.notFound().build();
    }

    // 특정 여행 수정
    @PutMapping("/{travelId}")
    @Operation(summary = "특정 여행 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "여행 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "여행 또는 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> modifyTravel(
            @Parameter(description = "수정할 여행 ID", example = "1e3a5081-675e-4264-8e56-ebb659e12acd")
            @PathVariable
            String travelId,
            @RequestBody TravelModifyRequest request
    ){
        //        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용

        travelService.updateTravel(travelId, userId, request);
        return ResponseEntity.noContent().build();
    }

    // 여행 목록 조회
    @GetMapping
    @Operation(summary = "여행 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 목록 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<TravelListResponse>> getTravelList(
            HttpServletRequest httpServletRequest,
            @Parameter(description = "조회할 여행 상태", example = "PLANNING")
            @RequestParam(required = false)
            TravelStatus status,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "pageNumber는 1 이상이어야 합니다.")
            int pageNumber,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize는 1 이상이어야 합니다.")
            int pageSize
    ){
        //        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(travelService.getTravelList(userId, status, pageNumber, pageSize));
    }

    // 특정 여행 조회
    @GetMapping("/{travelId}")
    @Operation(summary = "특정 여행 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 조회 성공",
                    content = @Content(schema = @Schema(implementation = TravelDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "여행을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TravelDetailResponse> getSpecificTravel(
            @Parameter(description = "조회할 여행 ID", example = "1e3a5081-675e-4264-8e56-ebb659e12acd")
            @PathVariable
            String travelId
    ){
        return ResponseEntity.ok(travelService.getTravelById(travelId));
    }

    // 여행 테마 조회
    @GetMapping("/theme")
    @Operation(summary = "여행 테마 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 테마 목록 조회 성공", useReturnTypeSchema = true)
    })
    public ResponseEntity<?> getTravelThemeList(
    ){
        return ResponseEntity.ok(travelService.getTravelThemeList());
    }

}
