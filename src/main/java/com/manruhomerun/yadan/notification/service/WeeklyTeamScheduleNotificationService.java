package com.manruhomerun.yadan.notification.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.baseball.repository.BaseballGameRepository;
import com.manruhomerun.yadan.notification.domain.enums.NotificationType;
import com.manruhomerun.yadan.notification.repository.NotificationRepository;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklyTeamScheduleNotificationService {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final String NOTIFICATION_TITLE = "이번 주 경기 일정";

    private final BaseballGameRepository baseballGameRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Transactional
    public int createWeeklyNotifications() {
        LocalDate weekStartDate = LocalDate.now(SEOUL_ZONE_ID)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime weekStartDateTime = weekStartDate.atStartOfDay();
        LocalDateTime nextWeekStartDateTime = weekStartDate.plusWeeks(1).atStartOfDay();

        Set<Long> teamIds = baseballGameRepository
                .findAllByGameDateGreaterThanEqualAndGameDateLessThanOrderByGameDateAscIdAsc(
                        weekStartDateTime,
                        nextWeekStartDateTime
                )
                .stream()
                .filter(game -> !Boolean.TRUE.equals(game.getIsCanceled()))
                .flatMap(this::getTeamIds)
                .collect(Collectors.toSet());

        if (teamIds.isEmpty()) {
            return 0;
        }

        int createdCount = 0;
        for (User user : userRepository.findWeeklyTeamScheduleNotificationTargets(teamIds)) {
            if (notificationRepository
                    .existsByUserIdAndTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                            user.getId(),
                            NotificationType.WEEKLY_TEAM_SCHEDULE,
                            weekStartDateTime,
                            nextWeekStartDateTime
                    )) {
                continue;
            }

            BaseballTeam favoriteTeam = user.getFavoriteTeam();
            notificationService.createNotification(
                    user,
                    NotificationType.WEEKLY_TEAM_SCHEDULE,
                    NOTIFICATION_TITLE,
                    "이번주 " + favoriteTeam.getTeamName() + "의 경기를 확인해보세요!",
                    favoriteTeam.getId().toString()
            );
            createdCount++;
        }

        return createdCount;
    }

    private Stream<Long> getTeamIds(BaseballGame game) {
        return Stream.of(
                game.getHomeTeam().getId(),
                game.getAwayTeam().getId()
        );
    }
}
