package com.manruhomerun.yadan.baseball.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "env.kbo")
public class KboScheduleProperties {
    private final String baseUrl;
}
