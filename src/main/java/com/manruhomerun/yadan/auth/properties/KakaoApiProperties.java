package com.manruhomerun.yadan.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "env.kakao")
public class KakaoApiProperties {

    private final Long appId;
    private final String apiBaseUrl;
}
