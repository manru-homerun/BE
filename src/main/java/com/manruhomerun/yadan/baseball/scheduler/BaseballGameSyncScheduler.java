package com.manruhomerun.yadan.baseball.scheduler;

import com.manruhomerun.yadan.baseball.service.BaseballGameCrwalingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseballGameSyncScheduler {

    private final BaseballGameCrwalingService baseballGameCrwalingService;

    @Scheduled(cron = "0 0 1 15 * *", zone = "Asia/Seoul")
    public void syncNextMonthSchedules() {
        baseballGameCrwalingService.syncNextMonthSchedules();
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void updatePreviousDayResults() {
        baseballGameCrwalingService.updatePreviousDayResults();
    }
}
