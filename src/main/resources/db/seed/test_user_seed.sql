START TRANSACTION;

INSERT INTO user (
    id,
    favorite_team,
    provider,
    provider_user_id,
    nickname,
    profile_image_url,
    gender,
    birthday,
    onboarding_completed,
    is_deleted,
    created_at,
    updated_at
)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    1,
    'KAKAO',
    'test-kakao-user-1',
    '테스트유저',
    'https://example.com/test-user-profile.png',
    'MALE',
    '1998-07-15',
    true,
    false,
    '2026-07-12 10:00:00',
    '2026-07-12 10:00:00'
)
ON DUPLICATE KEY UPDATE
    favorite_team = VALUES(favorite_team),
    provider = VALUES(provider),
    provider_user_id = VALUES(provider_user_id),
    nickname = VALUES(nickname),
    profile_image_url = VALUES(profile_image_url),
    gender = VALUES(gender),
    birthday = VALUES(birthday),
    onboarding_completed = VALUES(onboarding_completed),
    is_deleted = VALUES(is_deleted),
    updated_at = VALUES(updated_at);

COMMIT;
