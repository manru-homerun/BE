package com.manruhomerun.yadan.notification.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.manruhomerun.yadan.notification.service.WeeklyTeamScheduleNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyTeamScheduleNotificationScheduler {

    private final WeeklyTeamScheduleNotificationService weeklyTeamScheduleNotificationService;

    @Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Seoul")
    public void createWeeklyNotifications() {
        try {
            int createdCount = weeklyTeamScheduleNotificationService.createWeeklyNotifications();

            log.info(
                    "주간 경기 일정 알림 생성을 완료했습니다. createdCount={}",
                    createdCount
            );
        } catch (Exception exception) {
            log.error("주간 경기 일정 알림 생성에 실패했습니다.", exception);
            throw exception;
        }
    }
}
