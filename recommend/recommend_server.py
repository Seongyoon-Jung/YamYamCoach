from flask import Flask, request, jsonify
import json
import logging
import datetime
import pprint
from flask_cors import CORS
import pandas as pd
import os
import random # 추가
import numpy as np # 추가
import pymysql # DB동기화를 위해 추가
import warnings # DB동기화를 위해 추가

# recSys_demo.py 에서 필요한 함수 및 상수 임포트 -> 직접 코드로 통합했으므로 이 부분은 삭제됨
# from recSys_demo import (
#     load_food_data,
#     calculate_bmi,
#     calculate_personalized_eer,
#     calculate_macro_targets,
#     calculate_evening_targets,
#     recommend_dinner_menu,
#     # CATEGORY_COMBOS, # recommend_dinner_menu 내부에서 사용
#     # SINGLE_CATEGORIES # recommend_dinner_menu 내부에서 사용
# )

app = Flask(__name__)
# CORS 설정 수정 - 모든 요청 허용
CORS(app, 
     resources={r"/*": {
         "origins": "*", 
         "allow_headers": "*", 
         "expose_headers": "*", 
         "methods": ["GET", "POST", "OPTIONS"]
     }}, 
     supports_credentials=True)

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    handlers=[
        logging.StreamHandler(),  # 콘솔 출력
        logging.FileHandler(filename='recommend_server.log', encoding='utf-8')  # 파일 출력
    ]
)
logger = logging.getLogger(__name__)

# 수신 데이터 로그 저장 경로
LOG_FILE = "recommendation_requests.log"

# ── 경고 메시지 무시 (insertFoodDB.py 에서 가져옴) ──
warnings.filterwarnings("ignore", category=UserWarning, module="pandas")

# 카테고리 조합 정의 (recSys_demo.py 에서 가져옴)
CATEGORY_COMBOS = [
    ["국 및 탕류", "생채·무침류"], ["국 및 탕류", "볶음류"], ["국 및 탕류", "나물·숙채류"],
    ["국 및 탕류", "조림류"], ["국 및 탕류", "구이류"], ["국 및 탕류", "튀김류"],
    ["국 및 탕류", "전·적 및 부침류"], ["국 및 탕류", "장류, 양념류"], ["국 및 탕류", "장아찌·절임류"],
    ["국 및 탕류", "젓갈류"],
    ["찌개 및 전골류", "생채·무침류"], ["찌개 및 전골류", "볶음류"], ["찌개 및 전골류", "나물·숙채류"],
    ["찌개 및 전골류", "조림류"], ["찌개 및 전골류", "구이류"], ["찌개 및 전골류", "튀김류"],
    ["찌개 및 전골류", "전·적 및 부침류"], ["찌개 및 전골류", "장류, 양념류"], ["찌개 및 전골류", "장아찌·절임류"],
    ["찌개 및 전골류", "젓갈류"],
]
SINGLE_CATEGORIES = ["면 및 만두류", "죽 및 스프류", "밥류"]

# --- 음식 데이터 로드 함수 (recSys_demo.py에서 통합, 경로 수정) ---
def load_food_data():
    """ 음식 데이터를 CSV 파일에서 로드하여 DataFrame으로 반환합니다. """
    # 스크립트 파일의 위치를 기준으로 데이터 파일 경로 설정
    SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
    DATA_DIR = os.path.join(SCRIPT_DIR, 'data')
    
    info_csv_path = os.path.join(DATA_DIR, "foods_info.csv")
    nutrients_csv_path = os.path.join(DATA_DIR, "foods_nutrients.csv")
    
    required_cols_list = ['food_id', 'food_name', 'food_large_category', 'calories', 'protein', 'carbohydrate', 'fat', 'sugar', 'sodium']

    try:
        logger.info(f"음식 정보 CSV 로드 시도: {info_csv_path}")
        info_df = pd.read_csv(info_csv_path)
        logger.info(f"음식 영양 정보 CSV 로드 시도: {nutrients_csv_path}")
        nutrients_df = pd.read_csv(nutrients_csv_path)

        if 'food_name' in nutrients_df.columns and 'food_name' in info_df.columns:
            nutrients_df = nutrients_df.drop(columns=['food_name'])
        foods_df = pd.merge(info_df, nutrients_df, on="food_code", how="inner")

        expected_col_map = {
            'food_code': 'food_id',
            'energy_kcal': 'calories',
            'protein_g': 'protein',
            'carbohydrate_g': 'carbohydrate',
            'fat_g': 'fat',
            'sugar_g': 'sugar',
            'sodium_mg': 'sodium'
        }
        foods_df.rename(columns=expected_col_map, inplace=True)

        # required_cols 변수명을 required_cols_list로 변경했으므로 여기서도 수정
        missing_cols = [col for col in required_cols_list if col not in foods_df.columns]
        if missing_cols:
            logger.warning(f"병합 및 rename 후 DataFrame에 필수 컬럼이 누락: {missing_cols}. 사용 가능한 컬럼: {foods_df.columns.tolist()}")
        
        logger.info(f"CSV에서 {len(foods_df)}개의 음식 데이터를 성공적으로 로드하고 병합 (메모리).")
        return foods_df
    except FileNotFoundError as e:
        logger.error(f"음식 데이터 CSV 파일을 찾을 수 없습니다: {e.filename}. \n해결 시도된 전체 경로: {os.path.abspath(info_csv_path)}\n현재 작업 디렉토리: {os.getcwd()}")
        return pd.DataFrame(columns=required_cols_list)
    except Exception as e:
        logger.exception(f"CSV 데이터 로드 및 병합 중 오류 발생 (메모리): {info_csv_path}")
        return pd.DataFrame(columns=required_cols_list)

# --- (recSys_demo.py에서 가져온 나머지 함수들) ---
def select_pa_by_goal(weight, target_weight, gender):
    """
    Select PA based on weight goal:
    - diff >= 20: sedentary
    - 10 <= diff < 20: low active
    - 0 < diff < 10: active
    - diff <= 0: very active
    """
    diff = weight - target_weight
    if diff >= 20:
        return 1.0  # sedentary
    elif diff >= 10:
        return 1.11 if not gender else 1.12  # low active
    elif diff > 0:
        return 1.25 if not gender else 1.27  # active
    else:
        return 1.48 if not gender else 1.45  # very active

def calculate_eer(age, gender, weight_kg, height_cm, pa):
    """
    Calculate base EER using IOM formula with numeric PA.
    """
    height_m = height_cm / 100.0
    if not gender:  # False: male, True: female
        return 662 - (9.53 * age) + pa * ((15.91 * weight_kg) + (539.6 * height_m))
    else:
        return 354 - (6.91 * age) + pa * ((9.36 * weight_kg) + (726 * height_m))

def calculate_goal_factor(weight, target_weight):
    """
    Compute goal adjustment factor:
    - Weight loss: reduce intake by 5–20%
    - Weight gain: increase intake by 5–20%
    """
    diff = weight - target_weight
    if diff == 0: # 목표 체중과 현재 체중이 같으면 조정 없음
        return 1.0
    base = abs(diff) * 0.005
    factor = min(max(base, 0.05), 0.20)  # bound 5% to 20%
    return 1 - factor if diff > 0 else 1 + factor # diff > 0 이면 체중 감량

def calculate_personalized_eer(user_info):
    age = user_info['age']
    gender = user_info['gender'] # True: female, False: male
    weight = user_info['weight']
    target_weight = user_info['targetWeight']
    height = user_info['height']
    
    pa = select_pa_by_goal(weight, target_weight, gender)
    base_eer = calculate_eer(age, gender, weight, height, pa)
    goal_factor = calculate_goal_factor(weight, target_weight)
    personalized_eer = base_eer * goal_factor
    
    return {
        'personalized_eer': personalized_eer,
        'pa_used': pa,
        'goal_factor': goal_factor,
        'base_eer': base_eer
    }

def calculate_macro_targets(personalized_eer, disease_tags_str=""):
    disease_tags = [tag.strip().lower() for tag in disease_tags_str.split(',')]
    is_diabetic = 'diabetes' in disease_tags

    ratios = {
        'carbohydrate': (0.45, 0.65),
        'protein':      (0.15, 0.20) if is_diabetic else (0.10, 0.35),
        'fat':          (0.20, 0.35),
    }
    if is_diabetic:
        ratios['carbohydrate'] = (0.40, 0.55)

    sugar_max_ratio = 0.10
    cal_per_g = {'carbohydrate': 4, 'protein': 4, 'fat': 9, 'sugar': 4}
    
    targets = {}
    for macro, (low_ratio, high_ratio) in ratios.items():
        low_kcal  = personalized_eer * low_ratio
        high_kcal = personalized_eer * high_ratio
        targets[macro] = {
            'min_g': round(low_kcal  / cal_per_g[macro], 1),
            'max_g': round(high_kcal / cal_per_g[macro], 1)
        }
    sugar_max_kcal = personalized_eer * sugar_max_ratio
    targets['sugar'] = {
        'min_g': 0,
        'max_g': round(sugar_max_kcal / cal_per_g['sugar'], 1)
    }
    return targets

def calculate_bmi(weight_kg, height_cm):
    if height_cm <= 0: return 0
    height_m = height_cm / 100.0
    return round(weight_kg / (height_m ** 2), 1)

def calculate_evening_targets(daily_target_macros, lunch_nutrients):
    evening_targets = {}
    for nutrient, targets in daily_target_macros.items():
        lunch_val = lunch_nutrients.get(nutrient, 0)
        if 'min_g' in targets and 'max_g' in targets:
            evening_targets[nutrient] = {
                'min_g': max(0, targets['min_g'] - lunch_val),
                'max_g': max(0, targets['max_g'] - lunch_val)
            }
        elif 'max_g' in targets : # sugar
             evening_targets[nutrient] = {'max_g': max(0, targets['max_g'] - lunch_val)}
        elif 'max_mg' in targets: # sodium
            evening_targets[nutrient] = {'max_mg': max(0, targets['max_mg'] - lunch_nutrients.get('sodium',0))}
            
    return evening_targets

def filter_foods_by_conditions(foods_df, category, disease_tags, evening_targets, num_recommendations=1):
    if category:
        candidate_foods = foods_df[foods_df['food_large_category'] == category].copy()
    else: 
        candidate_foods = foods_df.copy()
        
    if candidate_foods.empty:
        return pd.DataFrame()

    candidate_foods['score'] = 0.0

    if 'diabetes' in disease_tags:
        candidate_foods['score'] -= candidate_foods['sugar'] / (candidate_foods['sugar'].max() + 1e-6)
    if 'hypertension' in disease_tags:
        candidate_foods['score'] -= candidate_foods['sodium'] / (candidate_foods['sodium'].max() + 1e-6)
    if 'hyperlipidemia' in disease_tags:
        candidate_foods['score'] -= candidate_foods['fat'] / (candidate_foods['fat'].max() + 1e-6)

    protein_needed_avg = (evening_targets.get('protein',{}).get('min_g',0) + evening_targets.get('protein',{}).get('max_g',0)) / 2
    carbs_needed_avg = (evening_targets.get('carbohydrate',{}).get('min_g',0) + evening_targets.get('carbohydrate',{}).get('max_g',0)) / 2

    if protein_needed_avg > 0:
        candidate_foods['score'] += (candidate_foods['protein'] / (candidate_foods['protein'].max() + 1e-6)) * 1.5
    if carbs_needed_avg > 0:
        candidate_foods['score'] += (candidate_foods['carbohydrate'] / (candidate_foods['carbohydrate'].max() + 1e-6)) * 1.0
    
    return candidate_foods.sort_values(by='score', ascending=False).head(num_recommendations)

def recommend_dinner_menu(user_info, lunch_nutrients, foods_df, daily_target_macros, evening_target_macros):
    # 이 함수의 로깅(print문)은 logger.info() 등으로 바꾸는 것이 서버 환경에 더 적합하나, 우선은 recSys_demo.py의 로직을 그대로 유지합니다.
    # 서버 환경에서는 print() 출력이 표준 출력으로 가며, 이는 Flask 로거와 별개로 동작할 수 있습니다.
    
    # bmi = calculate_bmi(user_info['weight'], user_info['height']) # API 핸들러에서 이미 계산 및 로깅됨
    # print(f"사용자 BMI: {bmi}") # API 핸들러에서 이미 로깅됨
    # if bmi > 25: user_info['diseaseTags'] = user_info.get('diseaseTags','') + ",obesity" # API 핸들러에서 이미 처리

    disease_tags_str = user_info.get('diseaseTags', "")
    parsed_disease_tags = [tag.strip().lower() for tag in disease_tags_str.split(',') if tag.strip()]

    # 일일 목표 칼로리 및 영양소 계산은 API 핸들러에서 이미 수행했으므로, 전달받은 daily_target_macros를 사용합니다.
    # eer_result = calculate_personalized_eer(user_info) # API 핸들러에서 호출
    # daily_eer = eer_result['personalized_eer'] # API 핸들러에서 계산
    # daily_target_macros = calculate_macro_targets(daily_eer, disease_tags_str) # API 핸들러에서 호출

    # 점심 섭취량 반영하여 저녁 목표 영양소 계산은 API 핸들러에서 이미 수행 후 evening_target_macros로 전달됨.

    recommendations = []
    recommendation_reasons = []
    
    if random.random() < 0.3 and SINGLE_CATEGORIES:
        chosen_category = random.choice(SINGLE_CATEGORIES)
        recommended_food_df = filter_foods_by_conditions(foods_df, chosen_category, parsed_disease_tags, evening_target_macros, 1)
        if not recommended_food_df.empty:
            food_item = recommended_food_df.iloc[0]
            recommendations.append({'name': food_item['food_name'], 'id': food_item['food_id']})
            reasons = [f"{chosen_category}에서 {food_item['food_name']}"]
            if 'diabetes' in parsed_disease_tags and food_item['sugar'] < (foods_df['sugar'].mean() / 2): reasons.append("당뇨 관리를 위해 당 함량이 낮은 편입니다.")
            if 'hypertension' in parsed_disease_tags and food_item['sodium'] < (foods_df['sodium'].mean() / 2): reasons.append("고혈압 관리를 위해 나트륨 함량이 낮은 편입니다.")
            protein_needed = (evening_target_macros.get('protein',{}).get('min_g',0) + evening_target_macros.get('protein',{}).get('max_g',0)) / 2
            if protein_needed > 10 and food_item['protein'] > foods_df['protein'].quantile(0.7): reasons.append("저녁에 필요한 단백질 보충에 도움됩니다.")
            recommendation_reasons.append(" ".join(reasons))
    else:
        if CATEGORY_COMBOS:
            chosen_combo = random.choice(CATEGORY_COMBOS)
            temp_recs = []
            temp_reasons_details = []
            for category_in_combo in chosen_combo:
                recommended_food_df = filter_foods_by_conditions(foods_df, category_in_combo, parsed_disease_tags, evening_target_macros, 1)
                if not recommended_food_df.empty:
                    food_item = recommended_food_df.iloc[0]
                    temp_recs.append({'name': food_item['food_name'], 'id': food_item['food_id']})
                    reason_detail = f"{category_in_combo}의 {food_item['food_name']}(은)는"
                    details = []
                    if 'diabetes' in parsed_disease_tags and food_item['sugar'] < (foods_df['sugar'].mean() * 0.7): details.append("당 함량이 비교적 낮고")
                    if 'hypertension' in parsed_disease_tags and food_item['sodium'] < (foods_df['sodium'].mean() * 0.7): details.append("나트륨 함량이 비교적 낮으며")
                    protein_needed = (evening_target_macros.get('protein',{}).get('min_g',0) + evening_target_macros.get('protein',{}).get('max_g',0)) / 2
                    if protein_needed > 10 and food_item['protein'] > foods_df['protein'].quantile(0.6): details.append("단백질 보충에 좋습니다.")
                    
                    if not details: temp_reasons_details.append(f"{reason_detail} 균형잡힌 선택입니다.")
                    else: temp_reasons_details.append(f"{reason_detail} {' '.join(details).strip()}")
            
            if temp_recs:
                recommendations.extend(temp_recs)
                recommendation_reasons.append(" ".join(temp_reasons_details))

    if not recommendations:
        # logger.info("카테고리 내 추천 실패. 전체 음식 데이터에서 확장 검색 시도...") # API 핸들러 로깅으로 대체 가능
        best_overall_food_df = filter_foods_by_conditions(foods_df, None, parsed_disease_tags, evening_target_macros, 1)
        if not best_overall_food_df.empty:
            food_item = best_overall_food_df.iloc[0]
            recommendations.append({'name': food_item['food_name'], 'id': food_item['food_id']})
            reason_detail = f"카테고리 조건에 맞는 음식을 찾지 못해 전체 음식 중 사용자님께 가장 적합할 것으로 판단되는 '{food_item['food_name']}'(을)를 추천합니다."
            details = []
            if 'diabetes' in parsed_disease_tags and food_item['sugar'] < (foods_df['sugar'].mean() * 0.8): details.append("당 함량이 비교적 낮고")
            if 'hypertension' in parsed_disease_tags and food_item['sodium'] < (foods_df['sodium'].mean() * 0.8): details.append("나트륨 함량이 비교적 낮으며")
            protein_needed = (evening_target_macros.get('protein',{}).get('min_g',0) + evening_target_macros.get('protein',{}).get('max_g',0)) / 2
            if protein_needed > 5 and food_item['protein'] > foods_df['protein'].quantile(0.5): details.append("단백질 보충에 도움될 수 있습니다.")
            
            if not details: recommendation_reasons.append(f"{reason_detail} 전반적인 영양 균형을 고려했습니다.")
            else: recommendation_reasons.append(f"{reason_detail} {' '.join(details).strip()}")
        else:
            recommendations.append({'name': "정보 부족으로 추천 불가", 'id': "N/A"})
            recommendation_reasons.append("모든 조건에서 적합한 음식을 찾지 못했습니다. 입력값을 확인하거나 관리자에게 문의하세요.")
        
    return {"recommended_dishes": recommendations, "reason": ". ".join(recommendation_reasons) + "."}

# --- DB 초기화 및 데이터 적재 함수 (insertFoodDB.py 로직 기반) ---
def initialize_database_from_csv():
    logger.info("데이터베이스 초기화 및 CSV 데이터 적재를 시작합니다...")
    db_conf = {
        "host": "127.0.0.1", # 실제 환경에 맞게 수정 필요
        "user": "ssafy",     # 실제 환경에 맞게 수정 필요
        "password": "ssafy", # 실제 환경에 맞게 수정 필요
        "database": "yamyam",  # 실제 환경에 맞게 수정 필요
        "charset": "utf8mb4"
    }
    
    # 스크립트 파일의 위치를 기준으로 데이터 파일 경로 설정
    SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
    DATA_DIR = os.path.join(SCRIPT_DIR, 'data')

    info_csv_path = os.path.join(DATA_DIR, "foods_info.csv")
    nutrients_csv_path = os.path.join(DATA_DIR, "foods_nutrients.csv")

    try:
        logger.info(f"[DB 작업 1/5] CSV 파일 로드 중... 경로: {info_csv_path}")
        df_info = pd.read_csv(info_csv_path)
        df_nutrients = pd.read_csv(nutrients_csv_path)
        logger.info("[DB 작업 1/5] CSV 파일 로드 완료.")
    except FileNotFoundError as e:
        logger.error(f"DB 초기화를 위한 CSV 파일을 찾을 수 없습니다: {e.filename}. 전체 경로: {os.path.abspath(e.filename)}")
        logger.error("DB 초기화 중단. 서버는 실행되지만 DB는 업데이트되지 않습니다.")
        return
    except Exception as e:
        logger.exception("DB 초기화를 위한 CSV 파일 로드 중 오류:")
        logger.error("DB 초기화 중단.")
        return

    conn = None # conn 변수 초기화
    try:
        logger.info("[DB 작업 2/5] MySQL 연결 시도...")
        conn = pymysql.connect(**db_conf)
        cur = conn.cursor()
        logger.info("[DB 작업 2/5] MySQL 연결 완료.")

        logger.info("[DB 작업 3/5] 기존 테이블 삭제 시도...")
        cur.execute("SET FOREIGN_KEY_CHECKS = 0")
        cur.execute("DROP TABLE IF EXISTS foods_nutrients")
        cur.execute("DROP TABLE IF EXISTS foods_info")
        cur.execute("SET FOREIGN_KEY_CHECKS = 1")
        logger.info("[DB 작업 3/5] 기존 테이블 삭제 완료.")

        logger.info("[DB 작업 4/5] 테이블 생성 및 데이터 삽입 시도...")
        # foods_info 테이블 생성
        cur.execute("""
            CREATE TABLE foods_info (
                f_id INT AUTO_INCREMENT PRIMARY KEY,
                food_code            VARCHAR(50)  NOT NULL UNIQUE,
                food_name            VARCHAR(100) NOT NULL,
                main_food_name       VARCHAR(100),
                food_large_category  VARCHAR(100),
                food_medium_category VARCHAR(100),
                food_small_category  VARCHAR(100),
                food_origin          VARCHAR(100)
            ) CHARSET=utf8mb4
        """)

        # foods_nutrients 테이블 생성
        cur.execute("""
            CREATE TABLE foods_nutrients (
                food_code       VARCHAR(50) PRIMARY KEY,
                food_name       VARCHAR(100),
                energy_kcal     FLOAT,
                water_g         FLOAT,
                protein_g       FLOAT,
                fat_g           FLOAT,
                ash_g           FLOAT,
                carbohydrate_g  FLOAT,
                sugar_g         FLOAT,
                dietary_fiber_g FLOAT,
                calcium_mg      FLOAT,
                iron_mg         FLOAT,
                phosphorus_mg   FLOAT,
                potassium_mg    FLOAT,
                sodium_mg       FLOAT,
                vitamin_a_mcg   FLOAT,
                retinol_mcg     FLOAT,
                beta_carotene_mcg FLOAT,
                thiamine_mg     FLOAT,
                riboflavin_mg   FLOAT,
                niacin_mg       FLOAT,
                vitamin_c_mg    FLOAT,
                vitamin_d_mcg   FLOAT,
                cholesterol_mg  FLOAT,
                saturated_fat_g FLOAT,
                trans_fat_g     FLOAT,
                vitamin_b12_mcg FLOAT,
                folic_acid_mcg  FLOAT,
                vitamin_e_mg    FLOAT,
                fructose_g      FLOAT,
                maltose_g       FLOAT,
                food_weight_g   FLOAT,
                FOREIGN KEY (food_code) REFERENCES foods_info(food_code) ON DELETE CASCADE
            ) CHARSET=utf8mb4
        """)

        # foods_info 데이터 삽입
        logger.info(f"foods_info 테이블에 {len(df_info)}개 데이터 삽입 시작...")
        for _, row in df_info.iterrows():
            cur.execute("""
                INSERT INTO foods_info
                    (food_code, food_name, main_food_name,
                     food_large_category, food_medium_category,
                     food_small_category, food_origin)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
            """, tuple(None if pd.isna(v) else v for v in row))
        logger.info("foods_info 데이터 삽입 완료.")

        # foods_nutrients 데이터 삽입
        logger.info(f"foods_nutrients 테이블에 {len(df_nutrients)}개 데이터 삽입 시작...")
        # 컬럼 순서가 CSV와 정확히 일치해야 함
        # df_nutrients.columns를 사용하여 동적으로 쿼리 생성
        cols_nutrients = ', '.join([f'`{col}`' for col in df_nutrients.columns]) # 백틱 추가
        val_placeholders = ', '.join(['%s'] * len(df_nutrients.columns))
        insert_sql_nutrients = f"INSERT INTO foods_nutrients ({cols_nutrients}) VALUES ({val_placeholders})"

        for _, row in df_nutrients.iterrows():
            cur.execute(insert_sql_nutrients, tuple(None if pd.isna(v) else v for v in row))
        logger.info("foods_nutrients 데이터 삽입 완료.")
        
        logger.info("[DB 작업 4/5] 테이블 생성 및 데이터 삽입 완료.")

        logger.info("[DB 작업 5/5] 변경사항 커밋 중...")
        conn.commit()
        logger.info("[DB 작업 5/5] 커밋 완료.")
        logger.info("✅ 데이터베이스 초기화 및 CSV 데이터 적재 작업 완료.")

    except pymysql.Error as db_err:
        logger.exception("MySQL 작업 중 오류 발생:")
        if conn:
            conn.rollback() # 오류 발생 시 롤백
            logger.info("롤백 수행됨.")
    except Exception as e:
        logger.exception("DB 초기화 및 데이터 적재 중 일반 오류 발생:")
    finally:
        if conn:
            conn.close()
            logger.info("MySQL 연결 종료.")

# --- 음식 데이터 전역 로드 ---
# recSys_demo.py의 load_food_data는 해당 파일 기준 상대 경로를 사용.
# recommend_server.py는 backend 폴더에서 실행되므로, 경로 조정 필요.
# recSys_demo.py의 load_food_data 함수가 './data/...'를 사용하고,
# recSys_demo.py 파일 자체가 recommend 폴더 하위에 있으므로,
# load_food_data()를 호출하면 (recommend_server.py의 실행 위치인 backend 기준)
# backend/recommend/data/... 를 올바르게 참조합니다.
foods_df_global = None

def initialize_food_data():
    global foods_df_global
    logger.info("음식 데이터 로드를 시작합니다...")
    try:
        # recSys_demo.py의 load_food_data를 직접 호출
        # 이 함수는 내부적으로 "recommend/data/" 경로를 사용하도록 수정되었거나,
        # 혹은 스크립트 위치 기준으로 "./data/"를 사용해왔다면,
        # 이 서버 스크립트가 backend/ 에서 실행되고, recSys_demo.py가 backend/recommend/ 에 있으므로
        # recSys_demo.py 내의 "./data/"는 backend/recommend/data/ 를 가리키게 됩니다.
        # 따라서 별도의 경로 수정 없이 호출 가능할 것으로 예상됩니다.
        foods_df_global = load_food_data()
        if foods_df_global.empty:
            logger.error("전역 음식 데이터 로드에 실패했거나 데이터가 비어있습니다.")
        else:
            logger.info(f"전역 음식 데이터 로드 완료. 총 {len(foods_df_global)}개 음식.")
    except Exception as e:
        logger.exception("전역 음식 데이터 로드 중 치명적 오류 발생:")

initialize_food_data() # 서버 시작 시 음식 데이터 로드

@app.route('/recommend', methods=['POST'])
def recommend_dinner_api():
    global foods_df_global
    try:
        request_data = request.get_json()
        # 요청 데이터 상세 로깅
        logger.info(f"요청 수신 시간: {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        logger.info(f"요청 데이터: {pprint.pformat(request_data)}")

        save_request_log(request_data) # 요청 로그 파일에 저장

        user_info = request_data.get('user', {})
        # API 요청에서는 'nutrients'로 오고, recSys_demo는 'lunch_nutrients'를 기대
        lunch_nutrients = request_data.get('nutrients', {}) 

        # 필수 사용자 정보 필드 검증
        required_user_fields = ['age', 'gender', 'weight', 'targetWeight', 'height']
        missing_user_fields = [field for field in required_user_fields if field not in user_info]
        if missing_user_fields:
            error_msg = f"사용자 정보 누락. 필수 필드: {', '.join(missing_user_fields)}"
            logger.error(error_msg)
            return jsonify({"error": "사용자 정보 누락", "message": error_msg}), 400

        # 필수 점심 영양소 정보 필드 처리: 누락된 경우 0으로 기본값 설정
        required_lunch_fields = ['calories', 'protein', 'carbohydrate', 'fat', 'sugar', 'sodium']
        for field in required_lunch_fields:
            if field not in lunch_nutrients:
                logger.warning(f"점심 영양소 정보 중 '{field}' 필드가 누락되어 0으로 기본값을 설정합니다.")
                lunch_nutrients[field] = 0
        
        if foods_df_global is None or foods_df_global.empty:
            logger.error("음식 데이터가 로드되지 않았거나 비어있어 추천을 진행할 수 없습니다.")
            # initialize_food_data() # 여기서 재시도? 아니면 서버 재시작 유도
            return jsonify({"error": "서버 내부 오류", "message": "추천 시스템 초기화 실패. 관리자에게 문의하세요."}), 500

        # BMI 계산 및 user_info에 질병 태그 동적 추가
        try:
            bmi = calculate_bmi(user_info['weight'], user_info['height'])
            logger.info(f"계산된 BMI: {bmi}")
            current_disease_tags = user_info.get('diseaseTags', '')
            # 문자열 태그를 리스트로 변환 후 obesity 추가/제거 용이하게
            tags_list = [tag.strip().lower() for tag in current_disease_tags.split(',') if tag.strip()]
            if bmi > 25:
                if 'obesity' not in tags_list:
                    tags_list.append('obesity')
                    user_info['diseaseTags'] = ",".join(tags_list)
                    logger.info(f"BMI 기반 'obesity' 태그 추가. 업데이트된 질병 태그: {user_info['diseaseTags']}")
            # BMI 정상화 시 obesity 태그 제거 (선택적)
            # elif bmi <= 25 and 'obesity' in tags_list:
            #     tags_list.remove('obesity')
            #     user_info['diseaseTags'] = ",".join(tags_list)
            #     logger.info(f"BMI 정상화로 'obesity' 태그 제거. 업데이트된 질병 태그: {user_info['diseaseTags']}")

        except Exception as e:
            logger.exception("BMI 계산 또는 질병 태그 업데이트 중 오류:")
            # BMI 계산 실패해도 추천은 진행될 수 있도록 할 수 있으나, 중요 정보이므로 오류 반환 고려
            return jsonify({"error": "사용자 데이터 처리 오류", "message": "BMI 계산 중 문제가 발생했습니다."}), 400


        # EER 및 일일 목표 영양소 계산
        eer_result = calculate_personalized_eer(user_info)
        daily_eer = eer_result['personalized_eer']
        daily_target_macros = calculate_macro_targets(daily_eer, user_info.get('diseaseTags', ""))
        logger.info(f"일일 목표 EER: {daily_eer:.2f} kcal, 목표 영양소: {daily_target_macros}")

        # 저녁 목표 영양소 계산
        evening_target_macros = calculate_evening_targets(daily_target_macros, lunch_nutrients)
        logger.info(f"저녁 목표 영양소: {evening_target_macros}")
        
        # 저녁 메뉴 추천 로직 호출
        dinner_recommendation_result = recommend_dinner_menu(
            user_info,
            lunch_nutrients,
            foods_df_global, # 전역으로 로드된 데이터 사용
            daily_target_macros,
            evening_target_macros
        )
        logger.info(f"추천 결과: {dinner_recommendation_result}")

        # API 응답 구성
        response = {
            "recommendations": dinner_recommendation_result.get("recommended_dishes", []),
            "reason": dinner_recommendation_result.get("reason", "추천 이유를 생성하지 못했습니다."),
            "user_profile": { # 사용자 관련 정보 그룹화
                "bmi": bmi,
                "disease_tags_updated": user_info.get('diseaseTags', '')
            },
            "nutrition_targets": { # 영양 목표 그룹화
                "daily_eer_kcal": round(daily_eer, 2),
                "daily_macros_gram": daily_target_macros,
                "evening_macros_gram": evening_target_macros
            },
            "timestamp": datetime.datetime.now().isoformat()
        }
        
        logger.info(f"최종 응답 데이터: {pprint.pformat(response)}")
        return jsonify(response)

    except FileNotFoundError as e: # 보통 initialize_food_data에서 발생해야 함
        logger.error(f"데이터 파일({e.filename})을 찾을 수 없습니다. 경로를 확인하세요.", exc_info=True)
        return jsonify({"error": "서버 설정 오류", "message": "추천에 필요한 데이터 파일을 찾을 수 없습니다."}), 500
    except ValueError as e: 
        logger.error(f"입력 데이터 값 오류: {str(e)}", exc_info=True)
        return jsonify({"error": "잘못된 입력 데이터", "message": str(e)}), 400
    except KeyError as e:
        logger.error(f"예상치 못한 키에 접근 시도: {str(e)}", exc_info=True)
        return jsonify({"error": "잘못된 요청 데이터 형식", "message": f"필수 데이터 필드가 누락되었거나 형식이 잘못되었습니다: {str(e)}"}), 400
    except Exception as e:
        logger.exception("추천 API 처리 중 알 수 없는 오류 발생")
        return jsonify({"error": "추천 처리 중 서버 내부 오류 발생", "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."}), 500

def save_request_log(request_data):
    try:
        with open(LOG_FILE, 'a', encoding='utf-8') as f: # encoding 명시
            log_entry = {
                "timestamp": datetime.datetime.now().isoformat(),
                "data": request_data
            }
            f.write(json.dumps(log_entry, ensure_ascii=False) + "\n") # ensure_ascii=False for Korean
    except Exception as e:
        logger.error(f"로그 저장 중 오류: {str(e)}", exc_info=True)

if __name__ == '__main__':
    # 1. 데이터베이스 초기화 및 CSV 데이터 적재 (서버 시작 시 1회)
    initialize_database_from_csv()

    # 2. Pandas DataFrame으로 음식 데이터 로드 (메모리 사용 위함)
    initialize_food_data() # 내부에서 load_food_data() 호출

    # 서버 시작 메시지
    print(" ")
    logger.info("="*50)
    logger.info("===== 식단 추천 시스템 서버 시작 (recSys_demo 연동, DB 자동 초기화) =====")
    if foods_df_global is None or foods_df_global.empty:
        logger.warning("경고: 인메모리 음식 데이터(Pandas DF)가 정상적으로 로드되지 않았습니다.")
    else:
        logger.info(f"인메모리 음식 데이터(Pandas DF) {len(foods_df_global)}개 로드 완료.")
    
    logger.info(f"요청 로그는 {LOG_FILE} 파일에 저장됩니다.")
    logger.info(f"Flask 서버 실행: host=0.0.0.0, port=5001, debug=True")
    logger.info("="*50)
    print(" ")
    
    app.run(host='0.0.0.0', port=5001, debug=True) 