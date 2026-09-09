package com.manruhomerun.yadan.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTokenInfoResponse(
        Long id,
        Long expiresInMillis,
        @JsonProperty("app_id")
        Long appId
) {
}
