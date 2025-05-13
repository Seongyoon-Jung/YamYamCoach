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
    print("[1/5] CSV 파일 로드")
    df_info = pd.read_csv('data/foods_info.csv')
    df_nutrients = pd.read_csv('data/foods_nutrients.csv')
    print("[1/5] CSV 파일 로드 완료")

    print("[2/5] MySQL 연결")
    db_conf = {
        "host": "127.0.0.1",
        "user": "ssafy",
        "password": "ssafy",
        "database": "yamyam",
        "charset": "utf8mb4"
    }
    conn = pymysql.connect(**db_conf)
    cur = conn.cursor()
    print("[2/5] MySQL 연결 완료")

    print("[3/5] 기존 테이블 삭제")
    cur.execute("SET FOREIGN_KEY_CHECKS = 0")
    cur.execute("DROP TABLE IF EXISTS foods_nutrients")
    cur.execute("DROP TABLE IF EXISTS foods_info")
    cur.execute("SET FOREIGN_KEY_CHECKS = 1")
    print("[3/5] 기존 테이블 삭제 완료")

    print("[4/5] 테이블 생성 및 데이터 삽입")
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

    # 데이터 삽입
    for _, row in df_info.iterrows():
        cur.execute("""
            INSERT INTO foods_info
                (food_code, food_name, main_food_name,
                 food_large_category, food_medium_category,
                 food_small_category, food_origin)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """, tuple(None if pd.isna(v) else v for v in row))

    for _, row in df_nutrients.iterrows():
        cur.execute(f"""
            INSERT INTO foods_nutrients ({', '.join(df_nutrients.columns)})
            VALUES ({', '.join(['%s'] * len(df_nutrients.columns))})
        """, tuple(None if pd.isna(v) else v for v in row))

    print("[4/5] 테이블 생성 및 데이터 삽입 완료")

    print("[5/5] 커밋 및 연결 종료")
    conn.commit()
    conn.close()
    print("✅ 작업 완료: CSV 파일의 데이터가 MySQL에 저장되었습니다.")

if __name__ == "__main__":
    main()