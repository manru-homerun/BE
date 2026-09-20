package com.manruhomerun.yadan.notification.domain.enums;

public enum NotificationType {
    FRIEND_REQUEST,
    FRIEND_REQUEST_ACCEPTED,
    TICKET_OPEN,
    // referenceId에는 응원팀 ID를 저장한다.
    WEEKLY_TEAM_SCHEDULE,
    VISIT_VERIFICATION_REMINDER,
    NEARBY_GAME
}
