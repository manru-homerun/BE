package com.manruhomerun.yadan.travel.controller;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.travel.scheduler.TravelDataProcessScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel/csv")
@Tag(name = "여행 CSV 테스트", description = "여행 데이터 CSV 수동 업로드 API")
public class TravelCsvTestController {

    private final TravelDataProcessScheduler travelDataProcessScheduler;

    @GetMapping
    @Operation(summary = "지정한 날짜에 종료된 여행의 CSV를 S3에 업로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "업로드 완료 또는 해당 날짜에 종료된 여행 없음"),
            @ApiResponse(responseCode = "400", description = "날짜 누락 또는 잘못된 날짜 형식",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "CSV 생성 또는 S3 업로드 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> uploadTravelCsv(
            @Parameter(description = "CSV로 업로드할 여행의 종료일", example = "2026-10-17", required = true)
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        travelDataProcessScheduler.uploadData(date);
        return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
    }
}
