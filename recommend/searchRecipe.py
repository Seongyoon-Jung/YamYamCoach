import requests
from bs4 import BeautifulSoup, NavigableString
import re
import pandas as pd
import os
from datetime import datetime
from langchain_openai import OpenAI
from langchain.prompts import PromptTemplate
import pymysql
import json
from dotenv import load_dotenv
import urllib.parse

# 환경변수 로드
load_dotenv()

# OpenAI API 키 설정
api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise ValueError("OPENAI_API_KEY가 설정되지 않았습니다. .env 파일에 API 키를 설정해주세요.")
os.environ["OPENAI_API_KEY"] = api_key

# DB 연결 설정
db_config = {
    "host": "localhost",
    "user": "ssafy",
    "password": "ssafy",
    "db": "yamyam",
    "charset": "utf8mb4"
}

# LangChain 설정
llm = OpenAI(temperature=0.7, max_tokens=2048)

# 레시피 정보 가공을 위한 프롬프트 템플릿
RECIPE_PROMPT = """
다음 레시피 정보를 분석하여 JSON 형식으로 변환해주세요:

제목: {title}
조리시간: {cooking_time}
인분: {servings}
재료: {ingredients}
조리순서: {cooking_steps}

다음 형식으로 변환해주세요:
{{
    "name": "레시피 이름",
    "description": "레시피 간단 설명 (2-3문장)",
    "difficulty": "초급" 또는 "중급" 또는 "고급",
    "category": "한식" 또는 "양식" 또는 "중식" 또는 "일식" 또는 "기타",
    "cook_time_minutes": 실제 예상 조리 시간(숫자만),
    "servings": 인분 수(숫자만),
    "ingredients": ["재료1", "재료2", ...],
    "content": "단계별 조리방법을 번호를 포함하여 서술"
}}

레시피의 난이도와 카테고리는 재료와 조리과정을 기반으로 판단해주세요.
"""

def search_recipe(food_name):
    """음식 이름으로 만개의 레시피에서 검색"""
    base_url = "https://www.10000recipe.com"
    encoded_food_name = urllib.parse.quote(food_name)
    search_url = f"{base_url}/recipe/list.html?q={encoded_food_name}"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    try:
        response = requests.get(search_url, headers=headers)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # 구조 변경에 맞춘 선택자
        recipe_link = soup.select_one('ul.common_sp_list_ul li.common_sp_list_li div.common_sp_thumb a')
        
        if recipe_link:
            recipe_url = base_url + recipe_link['href']
            print(f"'{food_name}'에 대한 레시피를 찾았습니다: {recipe_url}")
            return recipe_url
        else:
            print(f"'{food_name}'에 대한 레시피를 찾을 수 없습니다.")
            return None
    except requests.exceptions.RequestException as e:
        print(f"레시피 검색 중 오류 발생: {e}")
        return None
    except Exception as e:
        print(f"예상치 못한 오류 발생: {e}")
        return None

def crawl_recipe(url):
    """레시피 정보 크롤링 (음식명 추출 방식 개선)"""
    headers = {'User-Agent': 'Mozilla/5.0'}
    try:
        response = requests.get(url, headers=headers)
        soup = BeautifulSoup(response.text, 'html.parser')

        # ✅ 음식명(레시피명) 추출 (03_crawling.ipynb 참고)
        name_tag = soup.select_one('#relationGoods > div.best_tit > b:nth-child(1)')
        name = name_tag.text.strip() if name_tag else None

        # 인분
        servings_tag = soup.select_one('.view2_summary_info1')
        servings = re.sub(r'[^0-9]', '', servings_tag.text) if servings_tag else None

        # 조리 시간
        time_tag = soup.select_one('.view2_summary_info2')
        cooking_time = time_tag.text.strip() if time_tag else None

        # 재료
        ingredients = []
        ingredient_sections = soup.select('#divConfirmedMaterialArea > ul')
        for ul in ingredient_sections:
            for li in ul.select('li'):
                name_tag = li.select_one('.ingre_list_name')
                amount_tag = li.select_one('.ingre_list_ea')
                name_ = name_tag.text.strip() if name_tag else ''
                amount = amount_tag.text.strip() if amount_tag else ''
                name_ = re.sub(r'\s+', ' ', name_)
                amount = re.sub(r'\s+', ' ', amount)
                if name_:
                    ingredients.append(f"{name_} {amount}".strip())

        # 조리 순서 (하위 태그 제거)
        cooking_steps = []
        for i in range(1, 30):
            step_tag = soup.select_one(f'#stepdescr{i}')
            if not step_tag:
                break
            step_text = ''.join([str(content).strip() for content in step_tag.contents if isinstance(content, NavigableString)])
            step_text = re.sub(r'\s+', ' ', step_text).strip()
            if step_text:
                cooking_steps.append(step_text)

        # 이미지 URL
        image_url = soup.select_one('div.view_recipe_visual img')
        image_url = image_url['src'] if image_url else None

        # 결과 반환
        result = {
            'name': name,
            'cooking_time': cooking_time,
            'servings': servings,
            'ingredients': ingredients,
            'cooking_steps': cooking_steps,
            'image_url': image_url
        }
        print("\n=== 크롤링 결과 ===")
        print(f"음식명: {name}")
        print(f"조리시간: {cooking_time}")
        print(f"인분: {servings}")
        print(f"재료 수: {len(ingredients)}개")
        print(f"조리 단계: {len(cooking_steps)}단계")
        print(f"이미지 URL: {'있음' if image_url else '없음'}")
        print("==================\n")
        return result
    except Exception as e:
        print(f"크롤링 중 오류 발생: {e}")
        return None

def process_recipe_with_langchain(recipe_data):
    """LangChain을 사용하여 레시피 정보 가공"""
    prompt = PromptTemplate(
        input_variables=["title", "cooking_time", "servings", "ingredients", "cooking_steps"],
        template=RECIPE_PROMPT
    )

    # 프롬프트 생성
    formatted_prompt = prompt.format(
        title=recipe_data['name'],
        cooking_time=recipe_data['cooking_time'],
        servings=recipe_data['servings'],
        ingredients='\n'.join(recipe_data['ingredients']),
        cooking_steps='\n'.join(recipe_data['cooking_steps'])
    )

    # LLM으로 처리 (Deprecation Warning 해결)
    result = llm.invoke(formatted_prompt)

    # JSON 부분만 추출해서 파싱
    try:
        json_str = result
        match = re.search(r'\{.*\}', json_str, re.DOTALL)
        if match:
            json_str = match.group(0)
        processed_data = json.loads(json_str)
        # 추가 필드 설정
        processed_data['image_url'] = recipe_data['image_url']
        processed_data['likes'] = 0
        processed_data['user_id'] = 1  # 기본 사용자 ID
        processed_data['created_at'] = datetime.now()
        processed_data['updated_at'] = datetime.now()
        return processed_data
    except json.JSONDecodeError as e:
        print(f"JSON 파싱 오류: {e}")
        print("LLM 응답 원문:\n", result)
        return None

def check_recipe_exists(cursor, recipe_name):
    """DB에 동일한 이름의 레시피가 있는지 확인"""
    sql = "SELECT id FROM recipes WHERE name = %s"
    cursor.execute(sql, (recipe_name,))
    return cursor.fetchone() is not None

def save_to_db(recipe_data):
    """가공된 레시피 정보를 DB에 저장 (중복 체크 추가)"""
    conn = None
    try:
        conn = pymysql.connect(**db_config)
        with conn.cursor() as cursor:
            # 중복 체크
            if check_recipe_exists(cursor, recipe_data['name']):
                print(f"레시피 '{recipe_data['name']}'는 이미 DB에 존재합니다.")
                return False
                
            # 재료 데이터에서 대괄호 제거
            ingredients_str = json.dumps(recipe_data['ingredients'], ensure_ascii=False)
            ingredients_str = ingredients_str.replace('[', '').replace(']', '')
                
            sql = """
            INSERT INTO recipes (
                name, description, difficulty, category, cook_time_minutes,
                servings, ingredients, content, image_url, likes, user_id,
                created_at, updated_at
            ) VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
            )
            """
            
            cursor.execute(sql, (
                recipe_data['name'],
                recipe_data['description'],
                recipe_data['difficulty'],
                recipe_data['category'],
                recipe_data['cook_time_minutes'],
                recipe_data['servings'],
                ingredients_str,
                recipe_data['content'],
                recipe_data['image_url'],
                recipe_data['likes'],
                recipe_data['user_id'],
                recipe_data['created_at'],
                recipe_data['updated_at']
            ))
            
        conn.commit()
        print(f"레시피 '{recipe_data['name']}' DB 저장 완료")
        return True
        
    except Exception as e:
        print(f"DB 저장 중 오류 발생: {e}")
        if conn:
            conn.rollback()
        return False
    finally:
        if conn:
            conn.close()

def process_food_recipe(food_name):
    """음식 이름으로 레시피 검색, 크롤링, 가공, 저장 프로세스"""
    print(f"\n'{food_name}' 레시피 처리 시작...")
    
    # 1. 레시피 검색
    recipe_url = search_recipe(food_name)
    if not recipe_url:
        return False
    
    # 2. 크롤링
    recipe_data = crawl_recipe(recipe_url)
    if not recipe_data:
        return False
    
    # 3. LangChain으로 데이터 가공
    processed_data = process_recipe_with_langchain(recipe_data)
    if not processed_data:
        return False
    
    # 4. DB 저장
    return save_to_db(processed_data)

def main():
    while True:
        food_name = input("\n레시피를 검색할 음식 이름을 입력하세요 (종료하려면 'q' 입력): ")
        
        if food_name.lower() == 'q':
            print("프로그램을 종료합니다.")
            break
            
        if not food_name.strip():
            print("음식 이름을 입력해주세요.")
            continue
            
        success = process_food_recipe(food_name)
        if success:
            print(f"'{food_name}' 레시피 처리가 완료되었습니다.")
        else:
            print(f"'{food_name}' 레시피 처리 중 문제가 발생했습니다.")

if __name__ == "__main__":
    main()