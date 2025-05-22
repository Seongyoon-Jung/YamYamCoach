-- 유저 ID 1의 이전 4일간의 식단 기록 더미 데이터 생성
SET SQL_SAFE_UPDATES = 0;

-- 1. 기존 더미 데이터 제거 (user_id=1인 사용자의 지난 4일간 기록)
DELETE FROM dish_record 
WHERE user_id = 1 
AND DATE(recorded_at) BETWEEN DATE_SUB(CURDATE(), INTERVAL 4 DAY) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY);

-- 2. 어제 (CURDATE - 1) 데이터 - A코스 선택
-- 어제의 A코스 스케줄 ID 얻기
SET @yesterday = DATE_SUB(CURDATE(), INTERVAL 1 DAY);
SET @yesterday_schedule_A = (SELECT schedule_id FROM course_schedule WHERE schedule_date = @yesterday AND course_type = 'A');

-- 어제 A코스의 메뉴 중 일부 선택
INSERT INTO dish_record (user_id, schedule_id, dish_id, course_type, recorded_at)
SELECT 1, @yesterday_schedule_A, dish_id, 'A', TIMESTAMP(@yesterday)
FROM schedule_dish
WHERE schedule_id = @yesterday_schedule_A
-- 랜덤하게 4개 음식만 선택
AND serving_order IN (1, 2, 3, 6);

-- 3. 이틀 전 (CURDATE - 2) 데이터 - B코스 선택
SET @two_days_ago = DATE_SUB(CURDATE(), INTERVAL 2 DAY);
SET @two_days_ago_schedule_B = (SELECT schedule_id FROM course_schedule WHERE schedule_date = @two_days_ago AND course_type = 'B');

-- 이틀 전 B코스의 메뉴 중 일부 선택
INSERT INTO dish_record (user_id, schedule_id, dish_id, course_type, recorded_at)
SELECT 1, @two_days_ago_schedule_B, dish_id, 'B', TIMESTAMP(@two_days_ago)
FROM schedule_dish
WHERE schedule_id = @two_days_ago_schedule_B
-- 모든 음식 선택
AND serving_order BETWEEN 1 AND 6;

-- 4. 3일 전 (CURDATE - 3) 데이터 - A코스 선택
SET @three_days_ago = DATE_SUB(CURDATE(), INTERVAL 3 DAY);
SET @three_days_ago_schedule_A = (SELECT schedule_id FROM course_schedule WHERE schedule_date = @three_days_ago AND course_type = 'A');

-- 3일 전 A코스의 메뉴 중 일부 선택
INSERT INTO dish_record (user_id, schedule_id, dish_id, course_type, recorded_at)
SELECT 1, @three_days_ago_schedule_A, dish_id, 'A', TIMESTAMP(@three_days_ago)
FROM schedule_dish
WHERE schedule_id = @three_days_ago_schedule_A
-- 일부 음식만 선택
AND serving_order IN (1, 4, 5);

-- 5. 4일 전 (CURDATE - 4) 데이터 - B코스 선택
SET @four_days_ago = DATE_SUB(CURDATE(), INTERVAL 4 DAY);
SET @four_days_ago_schedule_B = (SELECT schedule_id FROM course_schedule WHERE schedule_date = @four_days_ago AND course_type = 'B');

-- 4일 전 B코스의 메뉴 중 일부 선택
INSERT INTO dish_record (user_id, schedule_id, dish_id, course_type, recorded_at)
SELECT 1, @four_days_ago_schedule_B, dish_id, 'B', TIMESTAMP(@four_days_ago)
FROM schedule_dish
WHERE schedule_id = @four_days_ago_schedule_B
-- 일부 음식만 선택
AND serving_order IN (1, 3, 5, 6);

-- 6. 기록 확인
SELECT 
    DATE(recorded_at) AS record_date,
    course_type,
    COUNT(*) AS dish_count,
    GROUP_CONCAT(dish_id) AS selected_dishes
FROM dish_record
WHERE user_id = 1
AND DATE(recorded_at) BETWEEN DATE_SUB(CURDATE(), INTERVAL 4 DAY) AND CURDATE()
GROUP BY DATE(recorded_at), course_type
ORDER BY record_date DESC;

-- Safe update mode 다시 활성화
SET SQL_SAFE_UPDATES = 1; 