package com.manruhomerun.yadan.global.client;

import java.time.LocalDateTime;

import com.manruhomerun.yadan.global.properties.GlobalProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordWebhookClient {

    private final GlobalProperties globalProperties;

    public void sendCrawlingFailure(String jobName, Throwable throwable) {
        String discordWebhookUrl = globalProperties.getWebhookUrl();

        if (discordWebhookUrl == null || discordWebhookUrl.isBlank()) {
            log.warn("디스코드 웹훅 URL이 비어 있어 알림을 전송하지 않습니다. jobName={}", jobName);
            return;
        }

        String message = """
                [야구 경기 백엔드 크롤링 실패]
                작업명: %s
                실패 시각: %s
                예외: %s
                메시지: %s
                """.formatted(
                jobName,
                LocalDateTime.now(),
                throwable.getClass().getSimpleName(),
                throwable.getMessage()
        );

        try {
            RestClient.create()
                    .post()
                    .uri(discordWebhookUrl)
                    .body(new DiscordWebhookRequest(message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            log.error("디스코드 웹훅 전송에 실패했습니다. jobName={}", jobName, exception);
        }
    }

    private record DiscordWebhookRequest(
            String content
    ) {
    }
}
