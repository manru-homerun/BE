package com.manruhomerun.yadan.global.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageResponse<T>(
        @Schema(description = "목록 데이터")
        List<T> contents,
        @Schema(description = "현재 페이지 번호", example = "1")
        int pageNumber,
        @Schema(description = "페이지 크기", example = "6")
        int pageSize,
        @Schema(description = "전체 데이터 수", example = "53")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "9")
        int totalPages
) {
    public static <T> PageResponse<T> from(Page<?> page, List<T> contents) {
        return new PageResponse<>(
                contents,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
