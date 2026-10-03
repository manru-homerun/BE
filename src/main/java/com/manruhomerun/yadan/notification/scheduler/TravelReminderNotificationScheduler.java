package com.manruhomerun.yadan.notification.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.manruhomerun.yadan.notification.service.TravelReminderNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TravelReminderNotificationScheduler {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final TravelReminderNotificationService travelReminderNotificationService;

    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Seoul") // 여행 시작 3일 전 20시 알림
    public void createD3TravelNotifications() {
        LocalDate targetDate = LocalDate.now(SEOUL_ZONE_ID).plusDays(3);

        try {
            int createdCount =
                    travelReminderNotificationService.createD3Notifications(targetDate);

            log.info(
                    "D-3 여행 알림 생성 완료. targetDate={}, createdCount={}",
                    targetDate,
                    createdCount
            );
        } catch (Exception exception) {
            log.error("D-3 여행 알림 생성 실패. targetDate={}", targetDate, exception);
            throw exception;
        }
    }
}
