package com.manruhomerun.yadan.travelspot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiTravelSpotRecommendResponse(
        List<Recommendation> recommendations
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Recommendation(
            @JsonProperty("content_id") String contentId,
            @JsonProperty("token_id") int tokenId,
            double score
    ) {
    }
}
