package com.manruhomerun.yadan.global.dto;

import com.manruhomerun.yadan.global.error.BaseErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 예외 응답")
public record ErrorResponse(
        @Schema(description = "에러 코드", example = "BASEBALL_GAME_404")
        String code,
        @Schema(description = "에러 메시지", example = "경기를 찾을 수 없습니다.")
        String message,
        @Schema(description = "요청 경로", example = "/api/baseball/1001")
        String path
) {
    public static ErrorResponse of(BaseErrorCode errorCode, String message, String path) {
        return new ErrorResponse(
                errorCode.getCode(),
                message,
                path
        );
    }
}
