START TRANSACTION;

INSERT INTO travel_spot (
    id,
    name,
    latitude,
    longitude,
    region_code,
    category,
    image
)
VALUES (
    '294505',
    '경국사(서울)',
    37.6139242251,
    127.0056310926,
    '11290',
    12,
    'http://tong.visitkorea.or.kr/cms/resource/81/3571681_image2_1.jpg'
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    latitude = VALUES(latitude),
    longitude = VALUES(longitude),
    region_code = VALUES(region_code),
    category = VALUES(category),
    image = VALUES(image);

COMMIT;
