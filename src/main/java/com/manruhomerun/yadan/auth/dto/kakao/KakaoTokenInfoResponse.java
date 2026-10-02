package com.manruhomerun.yadan.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTokenInfoResponse(
        Long id,
        @JsonProperty("expires_in")
        Long expiresInSeconds,
        @JsonProperty("app_id")
        Long appId
) {
}
