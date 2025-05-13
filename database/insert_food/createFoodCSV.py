#!/usr/bin/env python3
import warnings
import pandas as pd
import numpy as np

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
    print("[1/8] 데이터 로드 & 필터링 시작")
    df = pd.read_excel('data/20250408_FoodDB.xlsx')
    df = df[df['식품기원명'].str.contains('급식|가정식', na=False)]
    print("[1/8] 데이터 로드 완료, 필터링 적용")

    print("[2/8] 컬럼명 한글→영어 매핑")
    info_map = {
        '식품코드': 'food_code', '식품명': 'food_name',
        '대표식품명': 'main_food_name',
        '식품대분류명': 'food_large_category',
        '식품중분류명': 'food_medium_category',
        '식품소분류명': 'food_small_category',
        '식품기원명': 'food_origin'
    }
    df.rename(columns=info_map, inplace=True)
    print("[2/8] 매핑 완료")

    print("[3/8] food_code 생성")
    cats = sorted(df['food_large_category'].unique())
    prefix = {cat: chr(65 + i) for i, cat in enumerate(cats)}
    df['serial'] = (
        df.groupby('food_large_category')
          .cumcount().add(1)
          .astype(str).str.zfill(5)
    )
    df['food_code'] = df['food_large_category'].map(prefix) + df['serial']
    df.drop(columns=['serial'], inplace=True)
    print("[3/8] food_code 생성 완료")

    print("[4/8] '해당없음' → None 변환")
    for col in ['main_food_name','food_large_category','food_medium_category','food_small_category','food_origin']:
        df[col] = df[col].replace({'해당없음': None})
    print("[4/8] 변환 완료")

    print("[5/8] df_info 준비")
    df_info = df[['food_code','food_name','main_food_name',
                  'food_large_category','food_medium_category',
                  'food_small_category','food_origin']].copy()
    print("[5/8] df_info 준비 완료")

    print("[6/8] 영양소 컬럼명 매핑")
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
    print("[6/8] 매핑 테이블 준비 완료")

    print("[7/8] df_nutrients 준비")
    df_n = df[['food_code','food_name'] + list(nutrient_map.keys())].copy()
    df_n.rename(columns=nutrient_map, inplace=True)
    
    # 숫자 데이터 정제
    print("[7/8] 숫자 데이터 정제")
    df_n['food_weight_g'] = df_n['food_weight_g'].apply(clean_number)
    for col in df_n.columns.difference(['food_code', 'food_name']):
        df_n[col] = df_n[col].apply(lambda x: clean_number(x) or 0)
    print("[7/8] df_nutrients 준비 완료")

    print("[8/8] CSV 파일 저장")
    # NaN 값을 None으로 변환
    df_info = df_info.where(pd.notnull(df_info), None)
    df_n = df_n.where(pd.notnull(df_n), None)
    
    # CSV 파일로 저장
    df_info.to_csv('data/foods_info.csv', index=False, encoding='utf-8')
    df_n.to_csv('data/foods_nutrients.csv', index=False, encoding='utf-8')
    print("✅ 작업 완료: foods_info.csv와 foods_nutrients.csv 파일이 생성되었습니다.")

if __name__ == "__main__":
    main() 