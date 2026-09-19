package com.manruhomerun.yadan.travel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "AI 여행 코스 생성 응답")
public record AiTravelGenerateResponse(
        @JsonProperty("content_id_sequence")
        @Schema(description = "추천 순서대로 정렬된 여행지 ID 목록")
        List<String> contentIdSequence,

        @Schema(description = "추천된 여행 코스 단계 목록")
        List<StepResponse> steps
) {
    public record StepResponse(
            @Schema(description = "전체 추천 순위")
            int rank,

            @JsonProperty("day_index")
            @Schema(description = "여행 일차")
            int dayIndex,

            @JsonProperty("slot_index")
            @Schema(description = "해당 일차 내 순서")
            int slotIndex,

            @JsonProperty("content_id")
            @Schema(description = "여행지 ID")
            String contentId,

            @JsonProperty("token_id")
            @Schema(description = "AI 모델 토큰 ID")
            int tokenId,

            @Schema(description = "AI 추천 점수")
            double score
    ) {
    }
}
