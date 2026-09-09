package com.manruhomerun.yadan.baseball.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.manruhomerun.yadan.baseball.dto.BaseballGameDetailResponse;
import com.manruhomerun.yadan.baseball.dto.BaseballGameScheduleItemResponse;
import com.manruhomerun.yadan.baseball.service.BaseballService;
import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.global.dto.PageResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/baseball")
@Tag(name = "Baseball", description = "프로야구 경기 및 일정 조회 API")
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/{gameId}")
    @Operation(summary = "특정 경기 세부 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경기 조회 성공",
                    content = @Content(schema = @Schema(implementation = BaseballGameDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "경기를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BaseballGameDetailResponse> getGameDetail(
            @Parameter(description = "조회할 경기 ID", example = "1001")
            @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(baseballService.getGameDetail(gameId));
    }

//    @GetMapping("/stadiums/{stadiumId}/game-schedule")
//    @Operation(summary = "특정 구장 경기 일정 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "구장 경기 일정 조회 성공", useReturnTypeSchema = true),
//            @ApiResponse(responseCode = "404", description = "구장을 찾을 수 없음",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    public ResponseEntity<PageResponse<BaseballGameScheduleItemResponse>> getStadiumGameSchedules(
//            @Parameter(description = "조회할 구장 ID", example = "2")
//            @PathVariable Long stadiumId,
//            @Parameter(description = "조회 기준일, 미입력 시 오늘 날짜 사용", example = "2026-06-28")
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baselineDate,
//            @Parameter(description = "페이지 번호, 1부터 시작", example = "1")
//            @RequestParam(defaultValue = "1") int page
//    ) {
//        return ResponseEntity.ok(baseballService.getStadiumGameSchedules(stadiumId, baselineDate, page));
//    }

    @GetMapping("/teams/{teamId}/game-schedule")
    @Operation(summary = "특정 팀 경기 일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 경기 일정 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<BaseballGameScheduleItemResponse>> getTeamGameSchedules(
            @Parameter(description = "조회할 팀 ID", example = "1")
            @PathVariable Long teamId,
            @Parameter(description = "조회 기준일, 미입력 시 오늘 날짜 사용", example = "2026-06-28")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baselineDate,
            @Parameter(description = "페이지 번호, 1부터 시작", example = "1")
            @RequestParam(defaultValue = "1") int page
    ) {
        return ResponseEntity.ok(baseballService.getTeamGameSchedules(teamId, baselineDate, page));
    }
}
