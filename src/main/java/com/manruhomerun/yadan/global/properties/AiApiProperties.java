package com.manruhomerun.yadan.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "env.ai-api")
public class AiApiProperties {
    private final String baseUrl;
    private final String travelGeneratePath;
    private final String travelRecommendPath;
}
