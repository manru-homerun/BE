package com.manruhomerun.yadan.baseball.scheduler;

import com.manruhomerun.yadan.baseball.service.BaseballGameCrawlingService;
import com.manruhomerun.yadan.global.client.DiscordWebhookClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseballGameSyncScheduler {

    private final BaseballGameCrawlingService baseballGameCrawlingService;
    private final DiscordWebhookClient discordWebhookClient;

    @Scheduled(cron = "0 0 1 15 * *", zone = "Asia/Seoul")
    public void syncNextMonthSchedules() {
        runWithDiscordAlert("다음 달 경기 일정 동기화", baseballGameCrawlingService::syncNextMonthSchedules);
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void updatePreviousDayResults() {
        runWithDiscordAlert("전날 경기 결과 동기화", baseballGameCrawlingService::updatePreviousDayResults);
    }

    private void runWithDiscordAlert(String jobName, Runnable job) {
        try {
            job.run();
        } catch (Exception exception) {
            log.error("크롤링 작업 실행에 실패했습니다. jobName={}", jobName, exception);
            discordWebhookClient.sendCrawlingFailure(jobName, exception);
            throw exception;
        }
    }
}
