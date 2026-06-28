START TRANSACTION;

INSERT INTO baseball_game (
    id,
    stadium_id,
    home_team_id,
    away_team_id,
    game_date,
    away_team_score,
    home_team_score,
    game_type,
    is_canceled
)
VALUES
    (1001, 1, 1, 2, '2026-06-23 18:30:00', 5, 3, 'REGULAR', false),
    (1002, 3, 4, 8, '2026-06-23 18:30:00', 2, 6, 'REGULAR', false),
    (1003, 4, 5, 10, '2026-06-23 18:30:00', 4, 4, 'REGULAR', false),
    (1004, 5, 6, 9, '2026-06-23 18:30:00', 1, 7, 'REGULAR', false),
    (1005, 6, 7, 3, '2026-06-23 18:30:00', 3, 2, 'REGULAR', false),

    (1006, 1, 1, 2, '2026-06-24 18:30:00', 6, 8, 'REGULAR', false),
    (1007, 3, 4, 8, '2026-06-24 18:30:00', 3, 5, 'REGULAR', false),
    (1008, 4, 5, 10, '2026-06-24 18:30:00', 2, 1, 'REGULAR', false),
    (1009, 5, 6, 9, '2026-06-24 18:30:00', 5, 3, 'REGULAR', false),
    (1010, 6, 7, 3, '2026-06-24 18:30:00', 4, 6, 'REGULAR', false),

    (1011, 1, 1, 2, '2026-06-25 18:30:00', 3, 3, 'REGULAR', false),
    (1012, 3, 4, 8, '2026-06-25 18:30:00', 7, 4, 'REGULAR', false),
    (1013, 4, 5, 10, '2026-06-25 18:30:00', 6, 5, 'REGULAR', false),
    (1014, 5, 6, 9, '2026-06-25 18:30:00', 2, 0, 'REGULAR', false),
    (1015, 6, 7, 3, '2026-06-25 18:30:00', 1, 9, 'REGULAR', false),

    (1016, 2, 2, 4, '2026-06-26 18:30:00', 4, 7, 'REGULAR', false),
    (1017, 4, 5, 3, '2026-06-26 18:30:00', 2, 5, 'REGULAR', false),
    (1018, 7, 8, 6, '2026-06-26 18:30:00', 6, 4, 'REGULAR', false),
    (1019, 8, 9, 1, '2026-06-26 18:30:00', 5, 8, 'REGULAR', false),
    (1020, 9, 10, 7, '2026-06-26 18:30:00', 3, 1, 'REGULAR', false),

    (1021, 2, 2, 4, '2026-06-27 17:00:00', 1, 2, 'REGULAR', false),
    (1022, 4, 5, 3, '2026-06-27 17:00:00', 7, 3, 'REGULAR', false),
    (1023, 7, 8, 6, '2026-06-27 17:00:00', 2, 2, 'REGULAR', false),
    (1024, 8, 9, 1, '2026-06-27 17:00:00', 4, 6, 'REGULAR', false),
    (1025, 9, 10, 7, '2026-06-27 17:00:00', 5, 9, 'REGULAR', false),

    (1026, 2, 2, 4, '2026-06-28 14:00:00', 3, 1, 'REGULAR', false),
    (1027, 4, 5, 3, '2026-06-28 14:00:00', 4, 0, 'REGULAR', false),
    (1028, 7, 8, 6, '2026-06-28 14:00:00', 1, 3, 'REGULAR', false),
    (1029, 8, 9, 1, '2026-06-28 14:00:00', 2, 7, 'REGULAR', false),
    (1030, 9, 10, 7, '2026-06-28 14:00:00', 8, 6, 'REGULAR', false)
ON DUPLICATE KEY UPDATE
    stadium_id = VALUES(stadium_id),
    home_team_id = VALUES(home_team_id),
    away_team_id = VALUES(away_team_id),
    game_date = VALUES(game_date),
    away_team_score = VALUES(away_team_score),
    home_team_score = VALUES(home_team_score),
    game_type = VALUES(game_type),
    is_canceled = VALUES(is_canceled);

ALTER TABLE baseball_game AUTO_INCREMENT = 1031;

COMMIT;
