# Enhanced EER calculation reflecting distinct goals for weight loss vs. gain

import pprint
import pandas as pd
import random
import numpy as np

# 카테고리 조합 정의
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
SINGLE_CATEGORIES = ["면 및 만두류", "죽 및 스프류", "밥류"] # 밥류 추가

def load_food_data():
    """ 음식 데이터를 CSV 파일에서 로드하여 DataFrame으로 반환합니다. """
    try:
        # 상대 경로 사용 (스크립트가 워크스페이스 루트에서 실행된다고 가정)
        info_df = pd.read_csv("./data/foods_info.csv")
        nutrients_df = pd.read_csv("./data/foods_nutrients.csv")

        # food_code를 기준으로 병합
        # foods_info.csv의 food_name을 사용하고, nutrients_df의 food_name은 삭제 (중복 방지)
        if 'food_name' in nutrients_df.columns and 'food_name' in info_df.columns:
             nutrients_df = nutrients_df.drop(columns=['food_name'])
        foods_df = pd.merge(info_df, nutrients_df, on="food_code", how="inner")

        # 스크립트에서 사용하는 표준 컬럼명으로 변경
        expected_col_map = {
            'food_code': 'food_id', # food_code를 food_id로 사용
            # 'food_name'은 info_df의 것을 그대로 사용 (만약 컬럼명이 food_name이 아니라면 추가: '실제info의음식명컬럼': 'food_name')
            # 'food_large_category'도 info_df의 것을 그대로 사용 (만약 컬럼명이 food_large_category가 아니라면 추가)
            'energy_kcal': 'calories',
            'protein_g': 'protein',
            'carbohydrate_g': 'carbohydrate',
            'fat_g': 'fat',
            'sugar_g': 'sugar',
            'sodium_mg': 'sodium'
        }
        foods_df.rename(columns=expected_col_map, inplace=True)

        # 필수 컬럼 존재 여부 확인 (rename 후의 컬럼명 기준)
        required_cols = ['food_id', 'food_name', 'food_large_category', 'calories', 'protein', 'carbohydrate', 'fat', 'sugar', 'sodium']
        missing_cols = [col for col in required_cols if col not in foods_df.columns]
        if missing_cols:
            print(f"경고: 병합 및 rename 후 DataFrame에 필수 컬럼이 누락되었습니다: {missing_cols}.")
            print(f"사용 가능한 컬럼: {foods_df.columns.tolist()}")
            print("CSV 컬럼명, 병합 키, rename 로직을 확인하세요.")
            # raise ValueError(f"필수 컬럼 누락: {missing_cols}")

        print(f"CSV에서 {len(foods_df)}개의 음식 데이터를 성공적으로 로드하고 병합했습니다.")
        return foods_df
    except FileNotFoundError as e:
        print(f"오류: CSV 파일을 찾을 수 없습니다. 다음 경로를 확인하세요: {e.filename}")
        print("스크립트 실행 위치(워크스페이스 루트)와 파일 경로(./recommend/data/...)를 확인해주세요.")
        return pd.DataFrame(columns=['food_id', 'food_name', 'food_large_category', 'calories', 'protein', 'carbohydrate', 'fat', 'sugar', 'sodium'])
    except Exception as e:
        print(f"CSV 데이터 로드 및 병합 중 오류 발생: {e}")
        return pd.DataFrame(columns=['food_id', 'food_name', 'food_large_category', 'calories', 'protein', 'carbohydrate', 'fat', 'sugar', 'sodium'])

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

def calculate_personalized_eer(user_info): # request_data 대신 user_info를 직접 받도록 수정
    age = user_info['age']
    gender = user_info['gender'] # True: female, False: male
    weight = user_info['weight']
    target_weight = user_info['targetWeight']
    height = user_info['height']
    
    # 1. PA based on goal
    pa = select_pa_by_goal(weight, target_weight, gender)
    
    # 2. Base EER
    base_eer = calculate_eer(age, gender, weight, height, pa)
    
    # 3. Goal factor
    goal_factor = calculate_goal_factor(weight, target_weight)
    
    # 4. Final personalized EER
    personalized_eer = base_eer * goal_factor
    
    return {
        'personalized_eer': personalized_eer,
        'pa_used': pa,
        'goal_factor': goal_factor,
        'base_eer': base_eer
    }

# — 여기에 목표 칼로리 기반 매크로 계산 함수 추가 — 

def calculate_macro_targets(personalized_eer, disease_tags_str=""): # 질병 태그 인자 추가
    """
    Calculate recommended gram ranges for macros based on AMDR and WHO sugar guidelines.
    Adjusts ratios for diabetes.
    Returns a dict:
      {
        'carbohydrate': {'min_g':…, 'max_g':…},
        'protein':      {'min_g':…, 'max_g':…},
        'fat':          {'min_g':…, 'max_g':…},
        'sugar':        {'max_g':…}   # 최소는 0으로 고정
      }
    """
    disease_tags = [tag.strip().lower() for tag in disease_tags_str.split(',')]
    is_diabetic = 'diabetes' in disease_tags

    # AMDR 비율 범위 및 자유당 상한(%)
    ratios = {
        'carbohydrate': (0.45, 0.65),
        'protein':      (0.15, 0.20) if is_diabetic else (0.10, 0.35), # 당뇨병 환자 단백질 비율 조정
        'fat':          (0.20, 0.35),
    }
    # 당뇨병 환자의 경우 탄수화물 비율을 약간 낮추고, 단백질을 늘릴 수 있으나, 여기서는 단백질만 조정.
    # 필요시 의사 또는 영양사와 상담 필요. 대한당뇨병학회 가이드라인 참조 가능.
    if is_diabetic:
        ratios['carbohydrate'] = (0.40, 0.55) # 당뇨 환자 탄수화물 비율 조정 예시 (더 낮출 수도 있음)


    sugar_max_ratio = 0.10          # 자유당 <10%
    
    # 1g당 칼로리
    cal_per_g = {'carbohydrate': 4, 'protein': 4, 'fat': 9, 'sugar': 4}
    
    targets = {}
    for macro, (low_ratio, high_ratio) in ratios.items():
        low_kcal  = personalized_eer * low_ratio
        high_kcal = personalized_eer * high_ratio
        targets[macro] = {
            'min_g': round(low_kcal  / cal_per_g[macro], 1),
            'max_g': round(high_kcal / cal_per_g[macro], 1)
        }
    # 당류: 최소 0g, 최대 personalized_eer * sugar_max_ratio / 4g
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
                'min_g': max(0, targets['min_g'] - lunch_val), # 저녁 목표가 음수가 되지 않도록
                'max_g': max(0, targets['max_g'] - lunch_val)
            }
        elif 'max_g' in targets : # sugar
             evening_targets[nutrient] = {'max_g': max(0, targets['max_g'] - lunch_val)}
        elif 'max_mg' in targets: # sodium
            evening_targets[nutrient] = {'max_mg': max(0, targets['max_mg'] - lunch_nutrients.get('sodium',0))} # 점심 나트륨 반영
            
    # 저녁에 필요한 칼로리도 계산 (참고용)
    # 점심 칼로리를 daily_target_macros에 명시적으로 포함하지 않았으므로, nutrient 기반으로 추정
    # 여기서는 macro g 목표를 기반으로 저녁 식사 가이드라인을 정함
    return evening_targets

def filter_foods_by_conditions(foods_df, category, disease_tags, evening_targets, num_recommendations=1):
    """
    주어진 조건에 따라 음식을 필터링하고 우선순위에 따라 추천.
    - category: 필터링할 음식의 food_large_category. None일 경우 전체 음식에서 검색.
    - disease_tags: 사용자의 질병 태그 리스트
    - evening_targets: 저녁에 섭취해야 할 영양소 목표
    - num_recommendations: 해당 카테고리에서 추천할 음식 수
    """
    if category:
        candidate_foods = foods_df[foods_df['food_large_category'] == category].copy()
    else: # 카테고리가 None이면 전체 음식 데이터를 사용
        candidate_foods = foods_df.copy()
        
    if candidate_foods.empty:
        return pd.DataFrame() # 빈 DataFrame 반환

    # 점수 초기화
    candidate_foods['score'] = 0.0

    # 1. 질병 태그 기반 필터링 및 점수화 (낮을수록 좋음: - 점수, 높을수록 좋음: + 점수)
    if 'diabetes' in disease_tags:
        # 당뇨: 당류 낮은 음식 선호
        candidate_foods['score'] -= candidate_foods['sugar'] / (candidate_foods['sugar'].max() + 1e-6) # 정규화하여 빼기
    if 'hypertension' in disease_tags:
        # 고혈압: 나트륨 낮은 음식 선호
        candidate_foods['score'] -= candidate_foods['sodium'] / (candidate_foods['sodium'].max() + 1e-6)
    if 'hyperlipidemia' in disease_tags:
        # 고지혈증: 지방 낮은 음식 선호 (특히 포화지방, 여기서는 총 지방으로 단순화)
        candidate_foods['score'] -= candidate_foods['fat'] / (candidate_foods['fat'].max() + 1e-6)

    # 2. 저녁 목표 영양소 기반 점수화 (부족한 것을 채워주는 음식 선호)
    #    단백질 > 탄수화물 > 지방 순으로 중요도 가정
    protein_needed_avg = (evening_targets.get('protein',{}).get('min_g',0) + evening_targets.get('protein',{}).get('max_g',0)) / 2
    carbs_needed_avg = (evening_targets.get('carbohydrate',{}).get('min_g',0) + evening_targets.get('carbohydrate',{}).get('max_g',0)) / 2
    # fat_needed_avg = (evening_targets.get('fat',{}).get('min_g',0) + evening_targets.get('fat',{}).get('max_g',0)) / 2

    if protein_needed_avg > 0: # 단백질이 필요하면
        candidate_foods['score'] += (candidate_foods['protein'] / (candidate_foods['protein'].max() + 1e-6)) * 1.5 # 단백질 중요도 높게
    if carbs_needed_avg > 0: # 탄수화물이 필요하면
        candidate_foods['score'] += (candidate_foods['carbohydrate'] / (candidate_foods['carbohydrate'].max() + 1e-6)) * 1.0
    # if fat_needed_avg > 0: # 지방이 필요하면
        # candidate_foods['score'] += (candidate_foods['fat'] / (candidate_foods['fat'].max() + 1e-6)) * 0.5


    # 칼로리가 저녁 목표 범위에 근접한 음식 (너무 과하지 않게)
    # 저녁 예상 칼로리 (단순 합): protein * 4 + carbohydrate * 4 + fat * 9
    # 이 부분은 더 정교한 로직이 필요할 수 있음. 여기서는 위 점수제로 대체.

    # 점수가 높은 순으로 정렬하여 상위 N개 선택
    return candidate_foods.sort_values(by='score', ascending=False).head(num_recommendations)


def recommend_dinner_menu(user_info, lunch_nutrients, foods_df, daily_target_macros, evening_target_macros):
    """저녁 메뉴 추천 메인 로직"""
    # BMI 계산
    bmi = calculate_bmi(user_info['weight'], user_info['height'])
    print(f"사용자 BMI: {bmi}")
    if bmi > 25: user_info['diseaseTags'] = user_info.get('diseaseTags','') + ",obesity" # BMI 기반 비만 태그 추가 (예시)
    
    disease_tags_str = user_info.get('diseaseTags', "")
    parsed_disease_tags = [tag.strip().lower() for tag in disease_tags_str.split(',') if tag.strip()]

    # 1. 일일 목표 칼로리 및 영양소 계산
    eer_result = calculate_personalized_eer(user_info)
    daily_eer = eer_result['personalized_eer']
    daily_target_macros = calculate_macro_targets(daily_eer, disease_tags_str)

    print(f"\n일일 목표 칼로리: {daily_eer:.2f} kcal")
    print("일일 목표 영양소 (g/day):")
    for m, vals in daily_target_macros.items():
        if 'min_g' in vals: print(f"  {m.capitalize()}: {vals['min_g']:.1f}–{vals['max_g']:.1f} g")
        elif 'max_mg' in vals: print(f"  {m.capitalize()}: 최대 {vals['max_mg']:.1f} mg")
        else: print(f"  {m.capitalize()}: 최대 {vals['max_g']:.1f}g")

    # 2. 점심 섭취량 반영하여 저녁 목표 영양소 계산
    evening_target_macros = calculate_evening_targets(daily_target_macros, lunch_nutrients)
    print("\n저녁 목표 영양소 (남은 양, g/day):")
    for m, vals in evening_target_macros.items():
        if 'min_g' in vals: print(f"  {m.capitalize()}: {vals['min_g']:.1f}–{vals['max_g']:.1f} g")
        elif 'max_mg' in vals: print(f"  {m.capitalize()}: 최대 {vals['max_mg']:.1f} mg")
        else: print(f"  {m.capitalize()}: 최대 {vals['max_g']:.1f}g")

    # 3. 음식 카테고리 조합 선택
    recommendations = []
    recommendation_reasons = []
    
    # 단일 카테고리 또는 조합 카테고리 중 무작위 선택 (예시: 30% 확률로 단일)
    if random.random() < 0.3 and SINGLE_CATEGORIES:
        chosen_category = random.choice(SINGLE_CATEGORIES)
        recommended_food = filter_foods_by_conditions(foods_df, chosen_category, parsed_disease_tags, evening_target_macros, 1)
        if not recommended_food.empty:
            food_item = recommended_food.iloc[0]
            recommendations.append({'name': food_item['food_name'], 'id': food_item['food_id']}) # food_id 포함
            reasons = [f"{chosen_category}에서 {food_item['food_name']}"]
            # 상세 추천 이유 추가
            if 'diabetes' in parsed_disease_tags and food_item['sugar'] < (foods_df['sugar'].mean() / 2): reasons.append("당뇨 관리를 위해 당 함량이 낮은 편입니다.")
            if 'hypertension' in parsed_disease_tags and food_item['sodium'] < (foods_df['sodium'].mean() / 2): reasons.append("고혈압 관리를 위해 나트륨 함량이 낮은 편입니다.")
            protein_needed = (evening_target_macros.get('protein',{}).get('min_g',0) + evening_target_macros.get('protein',{}).get('max_g',0)) / 2
            if protein_needed > 10 and food_item['protein'] > foods_df['protein'].quantile(0.7): reasons.append("저녁에 필요한 단백질 보충에 도움됩니다.")
            recommendation_reasons.append(" ".join(reasons))

    else: # 조합 카테고리
        if CATEGORY_COMBOS:
            chosen_combo = random.choice(CATEGORY_COMBOS)
            temp_recs = []
            temp_reasons_details = []
            for category_in_combo in chosen_combo:
                recommended_food = filter_foods_by_conditions(foods_df, category_in_combo, parsed_disease_tags, evening_target_macros, 1)
                if not recommended_food.empty:
                    food_item = recommended_food.iloc[0]
                    temp_recs.append({'name': food_item['food_name'], 'id': food_item['food_id']}) # food_id 포함
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
        # 카테고리 내 추천 실패 시 전체 음식에서 가장 적합한 음식 1개 추천
        print("카테고리 내 추천 실패. 전체 음식 데이터에서 확장 검색 시도...")
        # category를 None으로 전달하여 전체 검색
        best_overall_food = filter_foods_by_conditions(foods_df, None, parsed_disease_tags, evening_target_macros, 1)
        if not best_overall_food.empty:
            food_item = best_overall_food.iloc[0]
            recommendations.append({'name': food_item['food_name'], 'id': food_item['food_id']}) # food_id 포함
            reason_detail = f"카테고리 조건에 맞는 음식을 찾지 못해 전체 음식 중 사용자에게 가장 적합할 것으로 판단되는 '{food_item['food_name']}'(을)를 추천합니다."
            details = []
            if 'diabetes' in parsed_disease_tags and food_item['sugar'] < (foods_df['sugar'].mean() * 0.8): details.append("당 함량이 비교적 낮고")
            if 'hypertension' in parsed_disease_tags and food_item['sodium'] < (foods_df['sodium'].mean() * 0.8): details.append("나트륨 함량이 비교적 낮으며")
            protein_needed = (evening_target_macros.get('protein',{}).get('min_g',0) + evening_target_macros.get('protein',{}).get('max_g',0)) / 2
            if protein_needed > 5 and food_item['protein'] > foods_df['protein'].quantile(0.5): details.append("단백질 보충에 도움될 수 있습니다.")
            
            if not details: recommendation_reasons.append(f"{reason_detail} 전반적인 영양 균형을 고려했습니다.")
            else: recommendation_reasons.append(f"{reason_detail} {' '.join(details).strip()}")
        else:
            # 이 경우에도 음식을 찾지 못하면 (매우 드문 경우)
            # 가장 간단한 기본값 또는 에러 메시지 (정책상 없어야하지만 안전장치)
            recommendations.append({'name': "정보 부족으로 추천 불가", 'id': "N/A"}) # food_id 포함
            recommendation_reasons.append("모든 조건에서 적합한 음식을 찾지 못했습니다. 입력값을 확인하거나 관리자에게 문의하세요.")
        
    return {"recommended_dishes": recommendations, "reason": ". ".join(recommendation_reasons) + "."}


# --- 메인 처리 로직 ---
def process_recommendation_request(request_data):
    print("\n" + "="*50)
    print("[입력 데이터]")
    pprint.pprint(request_data, width=100, depth=5) # depth 늘림
    print("="*50 + "\n")

    user_info = request_data.get('user', {})
    lunch_nutrients = request_data.get('lunch_nutrients', {}) # 점심 데이터 가져오기

    if not user_info or not lunch_nutrients :
        print("오류: 사용자 정보('user') 또는 점심 영양소 정보('lunch_nutrients')가 요청 데이터에 없습니다.")
        return None # 오류 시 None 반환

    required_user_fields = ['age', 'gender', 'weight', 'targetWeight', 'height']
    if any(field not in user_info for field in required_user_fields):
        print(f"오류: 사용자 정보 누락. 필수 필드: {', '.join(required_user_fields)}")
        return None # 오류 시 None 반환
    
    required_lunch_fields = ['calories', 'protein', 'carbohydrate', 'fat', 'sugar', 'sodium']
    if any(field not in lunch_nutrients for field in required_lunch_fields):
        print(f"오류: 점심 영양소 정보 누락. 필수 필드: {', '.join(required_lunch_fields)}")
        # return # 일단 진행하도록 주석 처리 (테스트 용이)

    foods_df = load_food_data() # 음식 데이터 로드

    if foods_df.empty:
        print("오류: 음식 데이터를 로드할 수 없어 추천을 중단합니다.")
        # recommend_dinner_menu가 이미 데이터 로드 실패 케이스를 다루므로, 여기서 바로 반환하거나, 
        # recommend_dinner_menu 호출 후 그 결과를 반환할 수 있음.
        # 여기서는 recommend_dinner_menu에 처리를 맡기고 호출.
        pass 

    # 사용자 정보 출력 및 업데이트 (BMI, EER 등)
    bmi = calculate_bmi(user_info['weight'], user_info['height'])
    print(f"사용자 BMI: {bmi}")
    # BMI 기반 질병 태그 업데이트 (recommend_dinner_menu 내부에서도 하지만, 여기서 user_info를 직접 업데이트)
    current_disease_tags = user_info.get('diseaseTags', '')
    if bmi > 25 and 'obesity' not in current_disease_tags.lower(): 
        user_info['diseaseTags'] = (current_disease_tags + ",obesity" if current_disease_tags else "obesity").strip(',')
        print(f"BMI 기반 질병 태그 업데이트: {user_info['diseaseTags']}")

    eer_result = calculate_personalized_eer(user_info)
    daily_eer = eer_result['personalized_eer']
    print(f"\n일일 목표 칼로리: {daily_eer:.2f} kcal")
    daily_target_macros = calculate_macro_targets(daily_eer, user_info.get('diseaseTags', ""))
    print("일일 목표 영양소 (g/day):")
    for m, vals in daily_target_macros.items():
        if 'min_g' in vals: print(f"  {m.capitalize()}: {vals['min_g']:.1f}–{vals['max_g']:.1f} g")
        elif 'max_mg' in vals: print(f"  {m.capitalize()}: 최대 {vals['max_mg']:.1f} mg")
        else: print(f"  {m.capitalize()}: 최대 {vals['max_g']:.1f}g")
    
    evening_target_macros = calculate_evening_targets(daily_target_macros, lunch_nutrients)
    print("\n저녁 목표 영양소 (남은 양, g/day):")
    for m, vals in evening_target_macros.items():
        if 'min_g' in vals: print(f"  {m.capitalize()}: {vals['min_g']:.1f}–{vals['max_g']:.1f} g")
        elif 'max_mg' in vals: print(f"  {m.capitalize()}: 최대 {vals['max_mg']:.1f} mg")
        else: print(f"  {m.capitalize()}: 최대 {vals['max_g']:.1f}g")
    print("="*50 + "\n")

    # recommend_dinner_menu 호출 시 필요한 정보 전달
    dinner_recommendation = recommend_dinner_menu(user_info, lunch_nutrients, foods_df, daily_target_macros, evening_target_macros)

    print("\n" + "="*50)
    print("[저녁 메뉴 추천 결과]")
    if dinner_recommendation and dinner_recommendation.get("recommended_dishes"):
        print("추천 메뉴:")
        for food_item in dinner_recommendation["recommended_dishes"]:
            print(f"  - {food_item['name']} (ID: {food_item['id']})")
        print(f"추천 이유: {dinner_recommendation['reason']}")
    else:
        # 이 경우는 recommend_dinner_menu에서 이미 ID가 포함된 메시지를 반환함
        if dinner_recommendation and "reason" in dinner_recommendation:
             print(f"추천 이유: {dinner_recommendation['reason']}")
        else:
            print("추천 메뉴를 생성하지 못했습니다.")
    print("="*50 + "\n")
    
    return dinner_recommendation


if __name__ == '__main__':
    sample_request_data_diabetes = {
    'user': {
            'name': '김당뇨', 'age': 55, 'gender': False, 
            'weight': 85, 'targetWeight': 75, 'height': 170,
            'diseaseTags': 'diabetes, hypertension'
        },
        'lunch_nutrients': {
            'calories': 600, 'protein': 25, 'carbohydrate': 80, 'fat': 20, 'sugar': 15, 'sodium': 1200
        }
    }

    sample_request_data_general_loss = {
        'user': {
            'name': '이건강', 'age': 30, 'gender': True,
            'weight': 65, 'targetWeight': 58, 'height': 165,
            'diseaseTags': ''
        },
        'lunch_nutrients': {
            'calories': 500, 'protein': 20, 'carbohydrate': 70, 'fat': 15, 'sugar': 10, 'sodium': 800
        }
    }
    
    sample_request_data_general_gain = {
        'user': {
            'name': '박증량', 'age': 25, 'gender': False,
            'weight': 60, 'targetWeight': 68, 'height': 175,
            'diseaseTags': 'hyperlipidemia' # 고지혈증 태그 추가
        },
        'lunch_nutrients': {
            'calories': 700, 'protein': 30, 'carbohydrate': 90, 'fat': 25, 'sugar': 20, 'sodium': 1000
        }
    }

    print("--- 당뇨 및 고혈압 환자 예시 ---")
    process_recommendation_request(sample_request_data_diabetes)
    
    print("\n--- 일반 사용자 예시 (체중 감량) ---")
    process_recommendation_request(sample_request_data_general_loss)

    print("\n--- 사용자 예시 (체중 증가 + 고지혈증) ---")
    process_recommendation_request(sample_request_data_general_gain)
    
    # 사용자 입력 부분 (기존 코드 활용)
    try:
        print("\n--- 사용자 입력 데이터로 계산 ---")
        custom_user_info = {}
        custom_lunch_nutrients = {}

        custom_user_info['name'] = input("이름: ")
        custom_user_info['age'] = int(input("나이 (숫자): "))
        gender_input = input("성별 (남성 m, 여성 f): ").lower()
        custom_user_info['gender'] = True if gender_input == 'f' else False
        custom_user_info['weight'] = float(input("현재 체중 (kg): "))
        custom_user_info['targetWeight'] = float(input("목표 체중 (kg): "))
        custom_user_info['height'] = float(input("키 (cm): "))
        custom_user_info['diseaseTags'] = input("질병 태그 (쉼표로 구분, 예: diabetes,hypertension): ")
        
        print("\n점심 식사 영양소 입력:")
        custom_lunch_nutrients['calories'] = float(input("점심 칼로리 (kcal): "))
        custom_lunch_nutrients['protein'] = float(input("점심 단백질 (g): "))
        custom_lunch_nutrients['carbohydrate'] = float(input("점심 탄수화물 (g): "))
        custom_lunch_nutrients['fat'] = float(input("점심 지방 (g): "))
        custom_lunch_nutrients['sugar'] = float(input("점심 당류 (g): "))
        custom_lunch_nutrients['sodium'] = float(input("점심 나트륨 (mg): "))
        
        custom_request_data = {'user': custom_user_info, 'lunch_nutrients': custom_lunch_nutrients}
        process_recommendation_request(custom_request_data)
    except ValueError:
        print("잘못된 입력입니다. 숫자 입력란에는 숫자를 입력해주세요.")
    except Exception as e:
        print(f"처리 중 오류 발생: {e}")
