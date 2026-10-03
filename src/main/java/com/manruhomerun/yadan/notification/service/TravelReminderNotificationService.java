package com.manruhomerun.yadan.notification.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.notification.domain.enums.NotificationType;
import com.manruhomerun.yadan.notification.repository.NotificationRepository;
import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import com.manruhomerun.yadan.travel.repository.TravelUserRepository;
import com.manruhomerun.yadan.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelReminderNotificationService {

    private static final String NOTIFICATION_TITLE = "여행이 3일 남았어요!";

    private final TravelUserRepository travelUserRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Transactional
    public int createD3Notifications(LocalDate targetDate) {
        List<TravelUser> targets =
                travelUserRepository.findAllReminderTargetsByTravelStartDate(targetDate);

        int createdCount = 0;
        for (TravelUser target : targets) {
            Travel travel = target.getTravel();
            User user = target.getUser();

            if (notificationRepository.existsByUserIdAndTypeAndReferenceId(
                    user.getId(),
                    NotificationType.TRAVEL_REMINDER_D3,
                    travel.getId()
            )) {
                continue;
            }

            notificationService.createNotification(
                    user,
                    NotificationType.TRAVEL_REMINDER_D3,
                    NOTIFICATION_TITLE,
                    travel.getName() + " 일정과 준비물을 미리 확인해 보세요.",
                    travel.getId()
            );
            createdCount++;
        }

        return createdCount;
    }
}
