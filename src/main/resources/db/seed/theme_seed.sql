START TRANSACTION;

INSERT INTO theme (id, name)
VALUES
    (1, '핫플레이스/감성'),
    (2, '도시/쇼핑'),
    (3, '문화/역사'),
    (4, '놀이/체험'),
    (5, '자연/액티비티'),
    (6, '휴식/웰니스'),
    (7, '콘텐츠/특별목적')
ON DUPLICATE KEY UPDATE
    name = VALUES(name);

ALTER TABLE theme AUTO_INCREMENT = 8;

COMMIT;
