/* ====================================================================== */
/* 0. SCHEMA INITIALIZATION                                              */
/* ====================================================================== */
DROP DATABASE IF EXISTS yamyam;
CREATE DATABASE yamyam CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yamyam;

/* ====================================================================== */
/* 1. USER – MEMBER BASIC INFO                                           */
/* ====================================================================== */
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    user_id        INT PRIMARY KEY AUTO_INCREMENT,
    email          VARCHAR(50)  NOT NULL UNIQUE,
    password       VARCHAR(60)  NOT NULL,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    gender         TINYINT      NOT NULL,      -- 0: male, 1: female
    birth_date     DATE         NOT NULL,
    height         INT,
    weight         INT,
    target_weight  INT,
    is_surveyed    TINYINT      DEFAULT 0,     -- 0: no, 1: yes
    diet_type      VARCHAR(50)  DEFAULT NULL,  -- e.g., weight_loss, muscle_gain
    role           VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- admin계정 생성 
-- admin@admin.com
-- !asdf1234

INSERT INTO `user`
  (email, password, username, gender, birth_date, height, weight, target_weight, is_surveyed, diet_type, role)
VALUES
  ('admin@admin.com', '$2a$10$Ul3upD/sqazPp0/1DGI1zu0fA.PBKfdzgBj4Qg4YzJY2e6Y/IxTxq', '관리자1', 0, '2000-01-01', '180', '90', '40', 0, NULL, 'ROLE_USER');

/* ====================================================================== */
/* 2. QUESTION – DIETARY SURVEY QUESTIONS                                */
/* ====================================================================== */
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
    question_id     INT PRIMARY KEY AUTO_INCREMENT,
    step_level      INT  NOT NULL,        -- 1 or 2
    question_text   TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `question` (step_level, question_text) VALUES
  (1,'나는 하루 세 끼를 규칙적으로 먹는다.'),
  (1,'나는 자주 야식을 먹는 편이다.'),
  (1,'나의 식사 시간은 거의 매일 비슷한 편이다.'),
  (1,'한 끼에 내가 먹는 양이 적절하다고 느낀다.'),
  (1,'나는 주로 채소와 단백질을 골고루 섭취하려고 노력한다.'),
  (1,'나는 단 음식을 자주 즐겨 먹는다.'),
  (1,'나는 일주일에 최소 3회 이상 규칙적으로 운동을 한다.'),
  (1,'나는 하루에 2L 이상의 물을 충분히 마신다.'),
  (1,'나는 스트레스를 받을 때 평소보다 더 많이 먹는 편이다.'),
  (1,'나는 식사할 때 TV·휴대폰·일 등 딴짓 없이 음식에만 집중해서 먹는다.');

/* ====================================================================== */
/* 3. SURVEY – USER SURVEY ANSWERS                                       */
/* ====================================================================== */
DROP TABLE IF EXISTS `survey`;
CREATE TABLE `survey` (
    survey_id     INT PRIMARY KEY AUTO_INCREMENT,
    user_id       INT NOT NULL,
    step_level    INT NOT NULL,
    survey_date   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    answer_values VARCHAR(100) NOT NULL,   -- e.g., '1,2,3,4,5'
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ====================================================================== */
/* 4. DISH – 개별 메뉴 영양 정보                                           */
/* ====================================================================== */
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
    dish_id            INT PRIMARY KEY AUTO_INCREMENT,
    dish_name          VARCHAR(100)   NOT NULL,
    calorie_kcal       DOUBLE,
    carbohydrate_g     DOUBLE DEFAULT 0,  -- 총 탄수화물
    sugar_g            DOUBLE,            -- 당류
    fiber_g            DOUBLE,            -- 식이섬유
    protein_g          DOUBLE DEFAULT 0,  -- 단백질
    fat_g              DOUBLE DEFAULT 0,  -- 지방
    saturated_fat_g    DOUBLE DEFAULT 0,  -- 포화지방
    trans_fat_g        DOUBLE DEFAULT 0,  -- 트랜스지방
    ash_g              DOUBLE,
    sodium_mg          DOUBLE,            -- 나트륨
    calcium_mg         DOUBLE,            -- 칼슘
    iron_mg            DOUBLE,
    phosphorus_mg      DOUBLE,
    potassium_mg       DOUBLE,
    vitamin_a_mcg      DOUBLE,
    vitamin_b1_mg      DOUBLE,
    vitamin_b2_mg      DOUBLE,
    vitamin_b3_mg      DOUBLE,
    folic_acid_mcg     DOUBLE,
    vitamin_c_mg       DOUBLE,
    vitamin_d_mcg      DOUBLE,
    vitamin_e_mg       DOUBLE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `dish` (
    dish_name, calorie_kcal, sugar_g, fiber_g, ash_g, sodium_mg,
    calcium_mg, iron_mg, phosphorus_mg, potassium_mg,
    vitamin_a_mcg, vitamin_b1_mg, vitamin_b2_mg, vitamin_b3_mg,
    folic_acid_mcg, vitamin_c_mg, vitamin_d_mcg, vitamin_e_mg
) VALUES
  ('우동',        350,  2.5, 1.2, 0.5, 800,  15, 1.2, 100, 200, 50, 0.1, 0.2, 1.5, 20, 3, 0.0, 0.5),
  ('김치',         30,  1.0, 2.0, 0.8, 500,  40, 2.0,  50, 250, 30, 0.2, 0.1, 0.5, 25,20, 0.0, 0.2),
  ('샐러드',       80,  3.0, 3.0, 0.7, 100,  25, 1.0,  60, 300, 60, 0.1, 0.1, 0.8, 30,25, 0.0, 1.0),
  ('만두',        160,  1.2, 1.0, 0.6, 300,  10, 1.5,  80, 150, 20, 0.2, 0.2, 2.0, 10, 2, 0.0, 0.3),
  ('야채튀김',    210,  1.5, 0.8, 1.0, 450,   5, 0.8,  70, 180, 15, 0.1, 0.1, 0.3,  8, 1, 0.0, 0.2),
  ('볶음밥',      520,  4.0, 2.0, 1.2, 900,  20, 2.5, 120, 350, 40, 0.3, 0.2, 3.0, 15, 4, 0.0, 0.6),
  ('채소볶음',    110,  2.0, 3.5, 0.9, 250,  30, 2.2,  90, 320, 35, 0.1, 0.1, 0.5, 18,15, 0.0, 0.8),
  ('스테이크',    650,  0.5, 0.0, 2.0, 750,  10, 3.0, 110, 400, 10, 0.3, 0.4, 5.0,  5, 0, 0.1, 2.0),
  ('현미밥',      330,  0.6, 2.0, 0.4,   5,  20, 1.0,  80, 270, 10, 0.1, 0.1, 1.0, 12, 1, 0.0, 0.4),
  ('과일샐러드',  120, 12.0,1.5, 0.5,   5,  15, 0.5,  60, 230,  5, 0.1, 0.1, 0.6, 45,45, 0.0, 1.2);

/* ====================================================================== */
/* 5. COURSE_SCHEDULE – 날짜별 A/B 코스 정의                             */
/* ====================================================================== */
DROP TABLE IF EXISTS `course_schedule`;
CREATE TABLE `course_schedule` (
    schedule_id   INT AUTO_INCREMENT PRIMARY KEY,
    schedule_date DATE           NOT NULL,
    course_type   CHAR(1)        NOT NULL,
    course_name   VARCHAR(100)   NOT NULL,
    UNIQUE(schedule_date, course_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `course_schedule` (schedule_date, course_type, course_name) VALUES
  ('2025-05-10','A','A코스: 우동 & 볶음밥'),
  ('2025-05-10','B','B코스: 만두 & 샐러드'),
  ('2025-05-11','A','A코스: 샐러드 백반'),
  ('2025-05-11','B','B코스: 스테이크 정식');

/* ====================================================================== */
/* 6. SCHEDULE_DISH – 코스별 세부 메뉴 매핑                               */
/* ====================================================================== */
DROP TABLE IF EXISTS `schedule_dish`;
CREATE TABLE `schedule_dish` (
    schedule_id   INT            NOT NULL,
    dish_id       INT            NOT NULL,
    serving_order INT            NOT NULL DEFAULT 1,
    note          VARCHAR(255)   DEFAULT NULL,
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP 
                                      ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (schedule_id, dish_id),
    FOREIGN KEY (schedule_id) REFERENCES `course_schedule`(schedule_id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id)     REFERENCES `dish`(dish_id)             ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
  (1,1,1,'메인: 우동'),
  (1,6,2,'사이드: 볶음밥'),
  (1,2,3,'반찬: 김치'),
  (1,7,4,'반찬: 채소볶음'),
  (1,5,5,'반찬: 야채튀김'),
  (2,4,1,'메인: 만두'),
  (2,3,2,'사이드: 샐러드'),
  (2,8,3,'사이드: 스테이크'),
  (2,2,4,'반찬: 김치'),
  (2,1,5,'토핑: 우동');

/* ====================================================================== */
/* 7. DISH_RECORD – 사용자 섭취 기록                                     */
/* ====================================================================== */
DROP TABLE IF EXISTS `dish_record`;
CREATE TABLE `dish_record` (
    record_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT           NOT NULL,
    schedule_id INT           NOT NULL,
    dish_id     INT           NOT NULL,
    recorded_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, schedule_id, dish_id),
    FOREIGN KEY (user_id)     REFERENCES `user`(user_id)          ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES `course_schedule`(schedule_id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id)     REFERENCES `dish`(dish_id)             ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `dish_record` (user_id, schedule_id, dish_id) VALUES
  (1,1,1),(1,1,6);

/* ====================================================================== */
/* 8. TODAY=2025-05-12용 추가 더미 & A코스 선택 기록(user_id=4)            */
/* ====================================================================== */
SET @today := '2025-05-12';
SET @user_id := 4;

INSERT INTO `course_schedule` (schedule_date, course_type, course_name)
VALUES
  (@today,'A','A코스: 우동 & 김치 & 샐러드'),
  (@today,'B','B코스: 만두 & 야채튀김');

SELECT @schedA := schedule_id
  FROM `course_schedule`
 WHERE schedule_date = @today AND course_type = 'A' LIMIT 1;

INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
  (@schedA,1,1,'메인: 우동'),
  (@schedA,2,2,'반찬: 김치'),
  (@schedA,3,3,'사이드: 샐러드');

-- user_id=4가 today A코스에서 dish_id 1,3을 체크
INSERT IGNORE INTO `dish_record` (user_id, schedule_id, dish_id) VALUES
  (@user_id,@schedA,1),
  (@user_id,@schedA,3);
  
  
# 오늘 식단 넣기
  
-- 오늘 날짜 변수 설정 (필요에 따라 '2025-05-12' 등으로 직접 지정할 수 있습니다)
SET @today := CURDATE();

-- 0) 이미 존재하는 오늘(A, B) 코스가 있으면 삭제 (course_schedule의 ON DELETE CASCADE로 schedule_dish도 함께 삭제됩니다)
DELETE FROM `course_schedule`
 WHERE schedule_date = @today
   AND course_type IN ('A','B');

-- 1) 오늘 코스 A/B 재삽입
INSERT INTO `course_schedule` (schedule_date, course_type, course_name)
VALUES
  (@today, 'A', 'A코스: 샘플 메뉴 5종'),
  (@today, 'B', 'B코스: 샘플 메뉴 5종');

-- 2) 방금 삽입된 schedule_id 조회
SELECT @schedA := schedule_id
  FROM `course_schedule`
 WHERE schedule_date = @today
   AND course_type = 'A'
 LIMIT 1;

SELECT @schedB := schedule_id
  FROM `course_schedule`
 WHERE schedule_date = @today
   AND course_type = 'B'
 LIMIT 1;

-- 3) A코스에 임의의 음식 5개 매핑 (dish_id는 기존에 정의된 ID 사용)
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
  (@schedA, 1, 1, '메인: 우동'),
  (@schedA, 2, 2, '반찬: 김치'),
  (@schedA, 3, 3, '사이드: 샐러드'),
  (@schedA, 4, 4, '사이드: 만두'),
  (@schedA, 5, 5, '사이드: 야채튀김');

-- 4) B코스에 임의의 음식 5개 매핑
INSERT INTO `schedule_dish` (schedule_id, dish_id, serving_order, note) VALUES
  (@schedB, 6, 1, '메인: 볶음밥'),
  (@schedB, 7, 2, '반찬: 채소볶음'),
  (@schedB, 8, 3, '메인: 스테이크'),
  (@schedB, 9, 4, '사이드: 현미밥'),
  (@schedB, 10,5, '사이드: 과일샐러드');
    
  select * from schedule_dish;
  
  
 select *
 from course_schedule s
 where schedule_date = date(now());

/* ====================================================================== */
/* 게시판 및 댓글 작성 테이블                                              */
/* ====================================================================== */
  
DROP TABLE if exists board; 
CREATE TABLE board (
    board_id       INT PRIMARY KEY AUTO_INCREMENT,
    user_id        INT NOT NULL,
    title          VARCHAR(255) NOT NULL,
    content        TEXT NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    view_count     INT DEFAULT 0,
    like_count     INT DEFAULT 0,
    image_url      VARCHAR(500),

    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE
);

DROP TABLE if exists `comment`;
CREATE TABLE `comment` (
    comment_id     INT PRIMARY KEY AUTO_INCREMENT,
    board_id       INT NOT NULL,
    user_id        INT NOT NULL,
    content        TEXT NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE
);

INSERT INTO board (
    user_id, title, content, created_at, updated_at, view_count, like_count, image_url
) VALUES
(
    1,
    '첫 번째 게시글 제목',
    '안녕하세요! 이것은 첫 번째 게시글 내용입니다.',
    NOW(), NOW(),
    10,
    1,
    'https://cdn.pixabay.com/photo/2020/03/16/12/46/food-4936947_1280.jpg'
),
(
    1,
    '두 번째 게시글 제목',
    '이것은 두 번째 게시글 내용이며, 이미지가 없습니다.',
    NOW(), NOW(),
    0,
    0,
    'https://cdn.pixabay.com/photo/2021/11/01/15/52/spring-roll-6760871_1280.jpg'
);

INSERT INTO comment (
    board_id, user_id, content, created_at, updated_at
) VALUES
(
    1,
    1,
    '첫 번째 게시글에 대한 첫 번째 댓글입니다.',
    NOW(), NOW()
),
(
    1,
    1,
    '두 번째 게시글에 달린 첫번째 댓글이에요!',
    NOW(), NOW()
);

select * from board;