package com.manruhomerun.yadan.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "env.global")
public class GlobalProperties {
    private final String webhookUrl;
}
