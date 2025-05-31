/* ====================================================================== */
/* 0. SCHEMA INITIALIZATION                                              */
/* ====================================================================== */
DROP DATABASE IF EXISTS yamyam;
CREATE DATABASE yamyam CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yamyam;

/* ====================================================================== */
/* 1. USER – MEMBER BASIC INFO                                           */
/* ====================================================================== */
DROP TABLE if exists user;
 
CREATE TABLE user (
    user_id        INT PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(60)  NOT NULL,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    gender         TINYINT      NOT NULL,      -- 0: male, 1: female
    birth_date     DATE         NOT NULL,
    height      INT,
    weight      INT,
    target_weight INT,
    is_surveyed    TINYINT      DEFAULT 0,     -- 0: no, 1: yes
    diet_type      VARCHAR(50)  DEFAULT NULL,   -- e.g., weight_loss, muscle_gain
    role           VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER'
);
/* Bcrypt로 해싱되서 패스워드를 저장해야하므로 스프링에서 inser해야함 */


/* ====================================================================== */
/* 2. QUESTION – DIETARY SURVEY QUESTIONS                                */
/* ====================================================================== */
CREATE TABLE question (
    question_id     INT PRIMARY KEY AUTO_INCREMENT,
    step_level      INT  NOT NULL,        -- 1 or 2
    question_text   TEXT NOT NULL
);

/* step-1 questions */
INSERT INTO question (step_level, question_text) VALUES
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
/* 6. SURVEY – USER SURVEY ANSWERS                                       */
/* ====================================================================== */
DROP TABLE if exists survey;

CREATE TABLE survey (
    survey_id     INT PRIMARY KEY AUTO_INCREMENT,
    user_id       INT NOT NULL,
    step_level    INT NOT NULL,
    survey_date   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    answer_values VARCHAR(100) NOT NULL,   -- e.g., '1,2,3,4,5'
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

/* Hong Gildong step-1 & step-2 */
-- INSERT INTO survey (user_id, step_level, survey_date, answer_values)
-- VALUES
-- (1,1,'2025-01-01','1,2,3,2,1'),
-- (1,2,'2025-04-01','2,3,2,3,2');

-- /* Lee Yumi step-1 */
-- INSERT INTO survey (user_id, step_level, survey_date, answer_values)
-- VALUES
-- (2,1,'2025-02-15','3,3,2,2,1');

select * from survey;
select * from user;
