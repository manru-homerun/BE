START TRANSACTION;

INSERT INTO baseball_stadium (id, name, latitude, longitude, region_code)
VALUES
    (1, '광주-KIA 챔피언스 필드', 35.168139, 126.889111, '12000'),
    (2, '잠실야구장', 37.512389, 127.071972, '11000'),
    (3, '대전 한화생명 볼파크', 36.316250, 127.431444, '30000'),
    (4, '수원 KT 위즈 파크', 37.299778, 127.009667, '41110'),
    (5, '고척 스카이돔', 37.498222, 126.867250, '11000'),
    (6, '대구삼성라이온즈파크', 35.840917, 128.681611, '27000'),
    (7, '인천 SSG 랜더스필드', 37.436778, 126.693306, '28000'),
    (8, '사직야구장', 35.194028, 129.061611, '26000'),
    (9, '창원 NC 파크', 35.222444, 128.581833, '48120')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    latitude = VALUES(latitude),
    longitude = VALUES(longitude),
    region_code = VALUES(region_code);

INSERT INTO baseball_team (id, home_stadium_id, team_name, logo_image)
VALUES
    (1, 1, 'KIA 타이거즈', 'kia_tigers.png'),
    (2, 2, 'LG 트윈스', 'lg_twins.png'),
    (3, 2, '두산 베어스', 'doosan_bears.png'),
    (4, 3, '한화 이글스', 'hanwha_eagles.png'),
    (5, 4, 'KT 위즈', 'kt_wiz.png'),
    (6, 5, '키움 히어로즈', 'kiwoom_heroes.png'),
    (7, 6, '삼성 라이온즈', 'samsung_lions.png'),
    (8, 7, 'SSG 랜더스', 'ssg_landers.png'),
    (9, 8, '롯데 자이언츠', 'lotte_giants.png'),
    (10, 9, 'NC 다이노스', 'nc_dinos.png')
ON DUPLICATE KEY UPDATE
    home_stadium_id = VALUES(home_stadium_id),
    team_name = VALUES(team_name),
    logo_image = VALUES(logo_image);

ALTER TABLE baseball_stadium AUTO_INCREMENT = 10;
ALTER TABLE baseball_team AUTO_INCREMENT = 11;

COMMIT;
