-- Safe update mode 비활성화
SET SQL_SAFE_UPDATES = 0;

-- 모든 음식에 영양소 값이 반드시 있도록 수정
-- 영양소 값이 NULL인 경우 임의의 값으로 업데이트
UPDATE dish SET
    calorie_kcal = COALESCE(calorie_kcal, FLOOR(100 + RAND() * 500)),
    protein_g = COALESCE(protein_g, FLOOR(5 + RAND() * 25)),
    carbohydrate_g = COALESCE(carbohydrate_g, FLOOR(10 + RAND() * 70)),
    fat_g = COALESCE(fat_g, FLOOR(2 + RAND() * 30)),
    sugar_g = COALESCE(sugar_g, FLOOR(1 + RAND() * 20))
WHERE dish_id BETWEEN 1 AND 10;

-- 각 음식별 특성에 맞는 영양소 값 설정 (기존 값 덮어쓰기)
-- 1. 우동
UPDATE dish SET
    calorie_kcal = 350,
    protein_g = 12,
    carbohydrate_g = 60,
    fat_g = 1.5,
    sugar_g = 2.5
WHERE dish_id = 1;

-- 2. 김치
UPDATE dish SET
    calorie_kcal = 30,
    protein_g = 2,
    carbohydrate_g = 6,
    fat_g = 0.5,
    sugar_g = 1.2
WHERE dish_id = 2;

-- 3. 샐러드
UPDATE dish SET
    calorie_kcal = 80,
    protein_g = 3,
    carbohydrate_g = 10,
    fat_g = 5,
    sugar_g = 4
WHERE dish_id = 3;

-- 4. 만두
UPDATE dish SET
    calorie_kcal = 160,
    protein_g = 8,
    carbohydrate_g = 20,
    fat_g = 6,
    sugar_g = 1.5
WHERE dish_id = 4;

-- 5. 야채튀김
UPDATE dish SET
    calorie_kcal = 210,
    protein_g = 4,
    carbohydrate_g = 25,
    fat_g = 12,
    sugar_g = 3
WHERE dish_id = 5;

-- 6. 볶음밥
UPDATE dish SET
    calorie_kcal = 520,
    protein_g = 15,
    carbohydrate_g = 80,
    fat_g = 15,
    sugar_g = 4
WHERE dish_id = 6;

-- 7. 채소볶음
UPDATE dish SET
    calorie_kcal = 110,
    protein_g = 5,
    carbohydrate_g = 15,
    fat_g = 3,
    sugar_g = 5
WHERE dish_id = 7;

-- 8. 스테이크
UPDATE dish SET
    calorie_kcal = 650,
    protein_g = 40,
    carbohydrate_g = 10,
    fat_g = 45,
    sugar_g = 1
WHERE dish_id = 8;

-- 9. 현미밥
UPDATE dish SET
    calorie_kcal = 330,
    protein_g = 7,
    carbohydrate_g = 70,
    fat_g = 2.5,
    sugar_g = 0.6
WHERE dish_id = 9;

-- 10. 과일샐러드
UPDATE dish SET
    calorie_kcal = 120,
    protein_g = 1.5,
    carbohydrate_g = 25,
    fat_g = 0.8,
    sugar_g = 20
WHERE dish_id = 10;

-- 5일간의 코스 스케줄 및 음식 매핑 더미 데이터

-- 기존 데이터 삭제
DELETE FROM `schedule_dish` WHERE schedule_id IN (
    SELECT schedule_id FROM `course_schedule` 
    WHERE schedule_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 4 DAY)
);

DELETE FROM `course_schedule` 
WHERE schedule_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 4 DAY);

-- 첫째 날 코스 스케줄
INSERT INTO `course_schedule` (schedule_date, course_type, course_name) VALUES
(CURDATE(), 'A', 'A코스: 한식 정식'),
(CURDATE(), 'B', 'B코스: 양식 정식');

-- 첫째 날 A코스 음식 매핑 (한식 정식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'A'), 9, 1, '메인: 현미밥'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'A'), 2, 2, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'A'), 7, 3, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'A'), 4, 4, '사이드: 만두'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'A'), 5, 5, '사이드: 야채튀김');

-- 첫째 날 B코스 음식 매핑 (양식 정식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'B'), 8, 1, '메인: 스테이크'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'B'), 3, 2, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'B'), 10, 3, '디저트: 과일샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'B'), 7, 4, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'B'), 5, 5, '사이드: 야채튀김'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = CURDATE() AND course_type = 'B'), 4, 6, '사이드: 만두');

-- 둘째 날 코스 스케줄
INSERT INTO `course_schedule` (schedule_date, course_type, course_name) VALUES
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'A', 'A코스: 일식 정식'),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'B', 'B코스: 중식 정식');

-- 둘째 날 A코스 음식 매핑 (일식 정식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'A'), 1, 1, '메인: 우동'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'A'), 3, 2, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'A'), 2, 3, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'A'), 5, 4, '사이드: 야채튀김'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'A'), 4, 5, '사이드: 만두'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'A'), 10, 6, '디저트: 과일샐러드');

-- 둘째 날 B코스 음식 매핑 (중식 정식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'B'), 6, 1, '메인: 볶음밥'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'B'), 4, 2, '사이드: 만두'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'B'), 7, 3, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'B'), 2, 4, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND course_type = 'B'), 5, 5, '사이드: 야채튀김');

-- 셋째 날 코스 스케줄
INSERT INTO `course_schedule` (schedule_date, course_type, course_name) VALUES
(DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'A', 'A코스: 건강식 정식'),
(DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'B', 'B코스: 혼합 정식');

-- 셋째 날 A코스 음식 매핑 (건강식 정식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'A'), 9, 1, '메인: 현미밥'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'A'), 3, 2, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'A'), 7, 3, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'A'), 2, 4, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'A'), 10, 5, '디저트: 과일샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'A'), 4, 6, '사이드: 만두'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'A'), 5, 7, '사이드: 야채튀김');

-- 셋째 날 B코스 음식 매핑 (혼합 정식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'B'), 8, 1, '메인: 스테이크'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'B'), 6, 2, '사이드: 볶음밥'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'B'), 3, 3, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'B'), 2, 4, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'B'), 7, 5, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 2 DAY) AND course_type = 'B'), 10, 6, '디저트: 과일샐러드');

-- 넷째 날 코스 스케줄
INSERT INTO `course_schedule` (schedule_date, course_type, course_name) VALUES
(DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'A', 'A코스: 한식 건강식'),
(DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'B', 'B코스: 양식 건강식');

-- 넷째 날 A코스 음식 매핑 (한식 건강식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'A'), 9, 1, '메인: 현미밥'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'A'), 7, 2, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'A'), 2, 3, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'A'), 3, 4, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'A'), 10, 5, '디저트: 과일샐러드');

-- 넷째 날 B코스 음식 매핑 (양식 건강식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'B'), 8, 1, '메인: 스테이크'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'B'), 3, 2, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'B'), 7, 3, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'B'), 9, 4, '사이드: 현미밥'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'B'), 10, 5, '디저트: 과일샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY) AND course_type = 'B'), 2, 6, '반찬: 김치');

-- 다섯째 날 코스 스케줄
INSERT INTO `course_schedule` (schedule_date, course_type, course_name) VALUES
(DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'A', 'A코스: 일식 건강식'),
(DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'B', 'B코스: 중식 건강식');

-- 다섯째 날 A코스 음식 매핑 (일식 건강식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'A'), 1, 1, '메인: 우동'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'A'), 3, 2, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'A'), 7, 3, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'A'), 2, 4, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'A'), 10, 5, '디저트: 과일샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'A'), 9, 6, '사이드: 현미밥');

-- 다섯째 날 B코스 음식 매핑 (중식 건강식)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'B'), 6, 1, '메인: 볶음밥'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'B'), 4, 2, '사이드: 만두'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'B'), 3, 3, '사이드: 샐러드'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'B'), 7, 4, '반찬: 채소볶음'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'B'), 2, 5, '반찬: 김치'),
((SELECT schedule_id FROM course_schedule WHERE schedule_date = DATE_ADD(CURDATE(), INTERVAL 4 DAY) AND course_type = 'B'), 10, 6, '디저트: 과일샐러드');

-- Safe update mode 다시 활성화
SET SQL_SAFE_UPDATES = 1; 