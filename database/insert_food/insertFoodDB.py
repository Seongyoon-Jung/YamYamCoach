#!/usr/bin/env python3
import warnings
import pandas as pd
import numpy as np
import pymysql

# ── 경고 메시지 무시 ──
warnings.filterwarnings("ignore", category=UserWarning, module="pandas")

def clean_number(x):
    """문자열에서 숫자와 온점만 남기고 float로 변환, 불가능하면 NaN 반환"""
    s = ''.join(ch for ch in str(x) if ch.isdigit() or ch == '.')
    try:
        return float(s) if s not in ('', '.') else np.nan
    except:
        return np.nan

def main():
    print("[1/14] 데이터 로드 & 필터링 시작")
    df = pd.read_excel('data/20250408_FoodDB.xlsx')
    df = df[df['식품기원명'].str.contains('급식|가정식', na=False)]
    print("[1/14] 데이터 로드 완료, 필터링 적용")

    print("[2/14] 컬럼명 한글→영어 매핑")
    info_map = {
        '식품코드': 'food_code', '식품명': 'food_name',
        '대표식품명': 'main_food_name',
        '식품대분류명': 'food_large_category',
        '식품중분류명': 'food_medium_category',
        '식품소분류명': 'food_small_category',
        '식품기원명': 'food_origin'
    }
    df.rename(columns=info_map, inplace=True)
    print("[2/14] 매핑 완료")

    print("[3/14] food_code 생성")
    cats = sorted(df['food_large_category'].unique())
    prefix = {cat: chr(65 + i) for i, cat in enumerate(cats)}
    df['serial'] = (
        df.groupby('food_large_category')
          .cumcount().add(1)
          .astype(str).str.zfill(5)
    )
    df['food_code'] = df['food_large_category'].map(prefix) + df['serial']
    df.drop(columns=['serial'], inplace=True)
    print("[3/14] food_code 생성 완료")

    print("[4/14] '해당없음' → None 변환")
    for col in ['main_food_name','food_large_category','food_medium_category','food_small_category','food_origin']:
        df[col] = df[col].replace({'해당없음': None})
    print("[4/14] 변환 완료")

    print("[5/14] df_info 준비")
    df_info = df[['food_code','food_name','main_food_name',
                  'food_large_category','food_medium_category',
                  'food_small_category','food_origin']].copy()
    print("[5/14] df_info 준비 완료")

    print("[6/14] 영양소 컬럼명 매핑")
    nutrient_map = {
        '에너지(kcal)': 'energy_kcal', '수분(g)': 'water_g',
        '단백질(g)': 'protein_g', '지방(g)': 'fat_g',
        '회분(g)': 'ash_g', '탄수화물(g)': 'carbohydrate_g',
        '당류(g)': 'sugar_g', '식이섬유(g)': 'dietary_fiber_g',
        '칼슘(mg)': 'calcium_mg', '철(mg)': 'iron_mg',
        '인(mg)': 'phosphorus_mg', '칼륨(mg)': 'potassium_mg',
        '나트륨(mg)': 'sodium_mg', '비타민 A(μg RAE)': 'vitamin_a_mcg',
        '레티놀(μg)': 'retinol_mcg', '베타카로틴(μg)': 'beta_carotene_mcg',
        '티아민(mg)': 'thiamine_mg', '리보플라빈(mg)': 'riboflavin_mg',
        '니아신(mg)': 'niacin_mg', '비타민 C(mg)': 'vitamin_c_mg',
        '비타민 D(μg)': 'vitamin_d_mcg', '콜레스테롤(mg)': 'cholesterol_mg',
        '포화지방산(g)': 'saturated_fat_g', '트랜스지방산(g)': 'trans_fat_g',
        '비타민 B12(μg)': 'vitamin_b12_mcg', '엽산(μg DFE)': 'folic_acid_mcg',
        '비타민 E(mg α-TE)': 'vitamin_e_mg', '과당(g)': 'fructose_g',
        '맥아당(g)': 'maltose_g', '식품중량': 'food_weight_g'
    }
    print("[6/14] 매핑 테이블 준비 완료")

    print("[7/14] df_nutrients 준비")
    df_n = df[['food_code','food_name'] + list(nutrient_map.keys())].copy()
    df_n.rename(columns=nutrient_map, inplace=True)
    print("[7/14] df_nutrients 준비 완료")

    print("[8/14] 수치형 변환 시작")
    df_n['food_weight_g'] = df_n['food_weight_g'].apply(clean_number)
    for col in df_n.columns.difference(['food_code','food_name','food_weight_g']):
        df_n[col] = df_n[col].apply(lambda x: clean_number(x) or 0)
    print("[8/14] 수치형 변환 완료")

    print("[9/14] NaN → None 변환")
    df_info = df_info.where(pd.notnull(df_info), None)
    df_n    = df_n   .where(pd.notnull(df_n),    None)
    print("[9/14] 결측치 변환 완료")

    print("[10/14] MySQL 연결 (127.0.0.1)")
    db_conf = {
        "host": "127.0.0.1",
        "user": "ssafy",
        "password": "ssafy",
        "database": "yamyam",
        "charset": "utf8mb4"
    }
    conn = pymysql.connect(**db_conf)
    cur  = conn.cursor()
    print("[10/14] 연결 성공")

    print("[11/14] FK 검사 끄고 기존 테이블 삭제")
    cur.execute("SET FOREIGN_KEY_CHECKS = 0")
    cur.execute("DROP TABLE IF EXISTS foods_nutrients")
    cur.execute("DROP TABLE IF EXISTS foods_info")
    cur.execute("SET FOREIGN_KEY_CHECKS = 1")
    print("[11/14] 삭제 완료")

    print("[12/14] foods_info 생성 & 삽입")
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
    ins_info = """
        INSERT INTO foods_info
          (food_code, food_name, main_food_name,
           food_large_category, food_medium_category,
           food_small_category, food_origin)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
    """
    records_info = [
        tuple(None if pd.isna(v) else v for v in row)
        for row in df_info.values
    ]
    cur.executemany(ins_info, records_info)
    print("[12/14] foods_info 삽입 완료")

    print("[13/14] foods_nutrients 생성 & 삽입")
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
    ins_nut = f"""
        INSERT INTO foods_nutrients ({', '.join(df_n.columns)})
        VALUES ({', '.join(['%s'] * len(df_n.columns))})
    """
    records_nut = [
        tuple(None if pd.isna(v) else v for v in row)
        for row in df_n.values
    ]
    cur.executemany(ins_nut, records_nut)
    print("[13/14] foods_nutrients 삽입 완료")

    print("[14/14] 커밋 & 종료")
    conn.commit()
    conn.close()
    print("✅ 작업 완료: 영어 칼럼명으로 변환된 info & nutrients가 MySQL에 저장되었습니다.")

if __name__ == "__main__":
    main()