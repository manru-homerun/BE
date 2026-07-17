package com.manruhomerun.yadan.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "env.tour-api")
public class TourApiProperties {
    private final String baseUrl;
    private final String mobileOs;
    private final String mobileApp;
    private final String serviceKey;
}
