package com.manruhomerun.yadan.baseball.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.baseball.service.BaseballGameCrawlingService;
import com.manruhomerun.yadan.global.dto.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/baseball/crawl")
@Tag(name = "Baseball Crawling - For Test", description = "프로야구 수동 크롤링 API")
public class BaseballCrawlingTestController {

    private final BaseballGameCrawlingService baseballGameCrawlingService;

    @PostMapping("/schedules")
    @Operation(summary = "지정한 날짜 범위의 경기 일정을 수동 크롤링")
    @ApiResponse(responseCode = "204", description = "경기 일정 크롤링 완료")
    public ResponseEntity<Void> crawlSchedules(
            @Parameter(description = "크롤링 시작일", example = "2026-08-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "크롤링 종료일", example = "2026-08-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        baseballGameCrawlingService.syncGameSchedules(startDate, endDate);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/results")
    @Operation(summary = "지정한 날짜 범위의 경기 결과를 수동 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "경기 결과 업데이트 완료"),
            @ApiResponse(responseCode = "400", description = "시작일이 종료일보다 늦음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> crawlResults(
            @Parameter(description = "결과 업데이트 시작일", example = "2026-07-16")
            @RequestParam(name = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "결과 업데이트 종료일", example = "2026-07-31")
            @RequestParam(name = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        baseballGameCrawlingService.updateGameResults(startDate, endDate);
        return ResponseEntity.noContent().build();
    }
}
