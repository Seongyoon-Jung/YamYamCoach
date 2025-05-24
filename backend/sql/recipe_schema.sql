-- 레시피 테이블 스키마 (MySQL)

-- 레시피 기본 정보 테이블
CREATE TABLE recipes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  category VARCHAR(50),
  cook_time_minutes INT,
  difficulty VARCHAR(20),
  servings INT,
  calories INT,
  protein FLOAT,
  carbs FLOAT,
  fat FLOAT,
  image_url VARCHAR(255),
  author_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  likes INT DEFAULT 0,
  FOREIGN KEY (author_id) REFERENCES users(id)
);

-- 레시피 재료 테이블
CREATE TABLE recipe_ingredients (
  id INT AUTO_INCREMENT PRIMARY KEY,
  recipe_id INT,
  name VARCHAR(100) NOT NULL,
  amount VARCHAR(50),
  unit VARCHAR(20),
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- 레시피 조리 단계 테이블
CREATE TABLE recipe_steps (
  id INT AUTO_INCREMENT PRIMARY KEY,
  recipe_id INT,
  step_order INT,
  title VARCHAR(255),
  description TEXT NOT NULL,
  image_url VARCHAR(255),
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- 레시피 태그 테이블
CREATE TABLE recipe_tags (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  UNIQUE (name)
);

-- 레시피-태그 연결 테이블 (다대다 관계)
CREATE TABLE recipe_tag_relations (
  recipe_id INT,
  tag_id INT,
  PRIMARY KEY (recipe_id, tag_id),
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
  FOREIGN KEY (tag_id) REFERENCES recipe_tags(id) ON DELETE CASCADE
);

-- 레시피 팁 테이블
CREATE TABLE recipe_tips (
  id INT AUTO_INCREMENT PRIMARY KEY,
  recipe_id INT,
  tip_text TEXT NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- 레시피 좋아요 테이블
CREATE TABLE recipe_likes (
  user_id INT,
  recipe_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, recipe_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- 레시피 영양 정보 상세 테이블 (선택적)
CREATE TABLE recipe_nutrition (
  recipe_id INT PRIMARY KEY,
  calories INT,
  protein FLOAT,
  carbs FLOAT,
  fat FLOAT,
  sugar FLOAT,
  sodium FLOAT,
  fiber FLOAT,
  cholesterol FLOAT,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_recipe_category ON recipes(category);
CREATE INDEX idx_recipe_difficulty ON recipes(difficulty);
CREATE INDEX idx_recipe_likes ON recipes(likes);
CREATE INDEX idx_recipe_calories ON recipes(calories);
CREATE INDEX idx_recipe_created ON recipes(created_at);

-- 샘플 데이터 추가 (테스트용)
INSERT INTO recipes (name, description, category, cook_time_minutes, difficulty, servings, calories, protein, carbs, fat, image_url)
VALUES 
('김치찌개', '매콤한 김치찌개 레시피입니다.', '한식', 30, '쉬움', 2, 350, 15, 30, 12, '/images/recipes/kimchi-stew.jpg'),
('토마토 파스타', '신선한 토마토로 만든 파스타', '양식', 25, '쉬움', 2, 420, 12, 65, 8, '/images/recipes/tomato-pasta.jpg'),
('닭가슴살 샐러드', '다이어트에 좋은 닭가슴살 샐러드', '샐러드', 15, '쉬움', 1, 250, 30, 10, 8, '/images/recipes/chicken-salad.jpg'),
('소고기 불고기', '달콤한 양념의 소불고기', '한식', 40, '보통', 4, 450, 28, 25, 20, '/images/recipes/bulgogi.jpg'),
('연어 스테이크', '오븐에 구운 건강한 연어 스테이크', '양식', 20, '보통', 2, 320, 35, 5, 18, '/images/recipes/salmon-steak.jpg');

-- 재료 데이터 예시
INSERT INTO recipe_ingredients (recipe_id, name, amount, unit)
VALUES
(1, '김치', '300', 'g'),
(1, '돼지고기', '200', 'g'),
(1, '두부', '1/2', '모'),
(1, '대파', '1', '대'),
(2, '스파게티 면', '200', 'g'),
(2, '토마토', '3', '개'),
(2, '마늘', '3', '쪽');

-- 조리 단계 데이터 예시
INSERT INTO recipe_steps (recipe_id, step_order, description)
VALUES
(1, 1, '김치를 적당한 크기로 썰어주세요.'),
(1, 2, '돼지고기를 먹기 좋은 크기로 썰어주세요.'),
(1, 3, '냄비에 물을 넣고 김치와 돼지고기를 넣어 끓여주세요.'),
(1, 4, '두부를 넣고 5분간 더 끓여주세요.'),
(2, 1, '물을 끓여 스파게티 면을 삶아주세요.'),
(2, 2, '토마토를 으깨어 소스를 만들어주세요.'),
(2, 3, '마늘을 볶다가 토마토 소스를 넣고 끓여주세요.'),
(2, 4, '삶은 면을 소스에 넣고 비벼주세요.');

-- 태그 데이터 예시
INSERT INTO recipe_tags (name)
VALUES
('매운맛'),
('다이어트'),
('고단백'),
('저탄수화물'),
('초간단'),
('건강식');

-- 레시피-태그 연결 데이터 예시
INSERT INTO recipe_tag_relations (recipe_id, tag_id)
VALUES
(1, 1), -- 김치찌개 - 매운맛
(2, 5), -- 토마토 파스타 - 초간단
(3, 2), -- 닭가슴살 샐러드 - 다이어트
(3, 3), -- 닭가슴살 샐러드 - 고단백
(4, 1), -- 소고기 불고기 - 매운맛
(5, 3), -- 연어 스테이크 - 고단백
(5, 6); -- 연어 스테이크 - 건강식 