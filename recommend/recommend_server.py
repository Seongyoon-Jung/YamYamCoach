from flask import Flask, request, jsonify
import json
import logging
import datetime
import pprint
from flask_cors import CORS

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

@app.route('/recommend', methods=['POST'])
def recommend_dinner():
    """
    저녁 메뉴 추천 API
    
    입력:
    - user: 사용자 정보 (성별, 나이, 키, 몸무게, 질병 태그 등)
    - nutrients: 오늘 섭취한 영양소 정보 (칼로리, 단백질, 탄수화물, 지방, 당)
    - mealCount: 오늘 섭취한 식사 수
    - dishIds: 오늘 섭취한 음식 ID 목록
    
    출력:
    - recommendation: 추천 메뉴
    - reason: 추천 이유
    """
    try:
        # 요청 헤더 출력
        print("\n" + "="*50)
        print("[요청 헤더]")
        print(request.headers)
        print("="*50 + "\n")
        
        # 요청 데이터 파싱
        request_data = request.get_json()
        
        # 콘솔에 요청 데이터를 더 보기 좋게 출력
        print("\n" + "="*50)
        print(f"[API 요청 수신 시간] {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("="*50)
        
        print("\n[요청 데이터 전체]")
        # pprint를 사용하여 더 보기 좋게 출력
        pprint.pprint(request_data, width=100, depth=4)
        
        print("\n[사용자 정보]")
        user_info = request_data.get('user', {})
        print(f"성별: {'여성' if user_info.get('gender') else '남성'}")
        print(f"나이: {user_info.get('age')}세")
        print(f"키: {user_info.get('height')}cm")
        print(f"몸무게: {user_info.get('weight')}kg")
        print(f"목표 몸무게: {user_info.get('targetWeight')}kg")
        
        # 페르소나 정보(disease_tags) 출력
        disease_tags = user_info.get('diseaseTags', '')
        if disease_tags:
            print(f"질병 태그: {disease_tags}")
        else:
            print("질병 태그 정보 없음")
        
        print("\n[오늘의 영양소 섭취 현황]")
        nutrients = request_data.get('nutrients', {})
        
        # 영양소 데이터 확인 및 디버깅 정보 출력
        if not nutrients or all(value == 0 for value in nutrients.values()):
            print("\033[93m경고: 영양소 데이터가 없거나 모두 0입니다!\033[0m")
            print("영양소 데이터 원본:", nutrients)
        
        print(f"칼로리: {nutrients.get('calories', 0):.1f}kcal")
        print(f"단백질: {nutrients.get('protein', 0):.1f}g")
        print(f"탄수화물: {nutrients.get('carbohydrate', 0):.1f}g")
        print(f"지방: {nutrients.get('fat', 0):.1f}g")
        print(f"당류: {nutrients.get('sugar', 0):.1f}g")
        
        # 음식 ID 목록 확인
        dish_ids = request_data.get('dishIds', [])
        if not dish_ids:
            print("\033[93m경고: 음식 ID 목록이 비어 있습니다!\033[0m")
        else:
            print(f"\n음식 ID 목록 (총 {len(dish_ids)}개): {dish_ids}")
        
        print(f"\n식사 횟수: {request_data.get('mealCount', 0)}회")
        print("="*50 + "\n")
        
        # 요청 데이터 로그 저장
        save_request_log(request_data)
        
        # 사용자 정보 및 영양소 정보 추출
        user_info = request_data.get('user', {})
        nutrients = request_data.get('nutrients', {})
        meal_count = request_data.get('mealCount', 0)
        dish_ids = request_data.get('dishIds', [])
        
        # 사용자 기본 정보
        gender = user_info.get('gender', None)  # true: 여성, false: 남성
        age = user_info.get('age', 0)
        height = user_info.get('height', 0)
        weight = user_info.get('weight', 0)
        target_weight = user_info.get('targetWeight', 0)
        disease_tags = user_info.get('diseaseTags', '').lower()
        
        # 질병 태그에 따른 추천 로직
        has_obesity = 'obesity' in disease_tags
        has_diabetes = 'diabetes' in disease_tags
        has_hypertension = 'hypertension' in disease_tags
        has_hyperlipidemia = 'hyperlipidemia' in disease_tags
        
        # 기본 추천값 설정
        recommendation = None
        reason = None
        
        # 당뇨 환자 추천
        if has_diabetes:
            if nutrients.get('carbohydrate', 0) > 200:
                recommendation = "닭 가슴살 샐러드와 현미밥 소량"
                reason = "당뇨 관리를 위해 탄수화물을 제한하고 단백질 위주의 식단을 추천합니다. 오늘 이미 탄수화물을 많이 섭취하셨으므로, 저녁에는 탄수화물이 적은 식단이 좋습니다."
            else:
                recommendation = "두부 스테이크와 잡곡밥, 데친 채소"
                reason = "당뇨 관리를 위해 복합 탄수화물과 식이섬유가 풍부한 식단을 추천합니다. 혈당 상승을 완만하게 하는 식단이 좋습니다."
        
        # 고혈압 환자 추천
        elif has_hypertension:
            recommendation = "저염 된장찌개와 현미밥, 구운 생선"
            reason = "고혈압 관리를 위해 소금 섭취를 제한하는 식단을 추천합니다. 칼륨이 풍부한 채소와 불포화지방이 풍부한 생선을 함께 섭취하면 혈압 관리에 도움이 됩니다."
        
        # 고지혈증 환자 추천
        elif has_hyperlipidemia:
            recommendation = "오트밀 죽과 연어 구이, 아보카도 샐러드"
            reason = "고지혈증 관리를 위해 불포화지방산이 풍부하고 콜레스테롤이 낮은 식단을 추천합니다. 섬유질이 풍부한 오트밀은 콜레스테롤 수치를 낮추는데 도움이 됩니다."
        
        # 비만 환자 추천
        elif has_obesity:
            if nutrients.get('calories', 0) > 1500:
                recommendation = "닭 가슴살 샐러드와 저칼로리 수프"
                reason = "체중 관리를 위해 오늘 남은 칼로리 섭취를 제한하는 저칼로리 고단백 식단을 추천합니다. 오늘 이미 많은 칼로리를 섭취하셨으므로, 가벼운 저녁 식사가 좋습니다."
            else:
                recommendation = "토마토 닭가슴살 스튜와 귀리밥"
                reason = "체중 관리를 위해 칼로리는 적지만 포만감이 높은 식단을 추천합니다. 단백질과 섬유질이 균형있게 포함된 식단이 체중 감량에 도움이 됩니다."
        
        # 질병 태그가 없거나 위에서 처리되지 않은 경우 기본 로직
        if recommendation is None:
            # 나이 기반 추천 로직
            if age < 30:
                recommendation = "그릴 치킨 샐러드와 호박스프"
                reason = "대사량이 좋은 20대에게는 단백질이 충분한 가볍고 건강한 식사를 추천합니다."
            elif age < 50:
                recommendation = "연어 스테이크와 구운 야채"
                reason = "30-40대에게는 오메가3가 풍부한 생선과 섬유질 위주의 균형잡힌 식사를 추천합니다."
            else:
                recommendation = "현미밥과 된장찌개"
                reason = "50대 이상에게는 소화가 잘 되고 영양 밸런스가 좋은 한식을 추천합니다."
            
            # 성별 기반 추가 추천
            if gender == True:  # 여성
                recommendation += ", 그리고 과일 디저트"
                reason += " 여성에게는 칼슘과 철분이 풍부한 식단이 좋습니다."
            else:  # 남성
                recommendation += ", 그리고 통곡물 빵"
                reason += " 남성에게는 충분한 단백질과 탄수화물의 균형이 중요합니다."
        
        # 키와 몸무게 기반 BMI 계산
        if height > 0 and weight > 0:
            bmi = weight / ((height / 100) ** 2)
            
            if bmi < 18.5:
                reason += " 현재 BMI가 낮은 편이므로 건강한 탄수화물과 단백질 위주로 추천합니다."
            elif bmi < 23:
                reason += " 현재 BMI가 정상 범위이므로 균형 잡힌 식단을 유지하는 것이 좋습니다."
            elif bmi < 25:
                reason += " 현재 BMI가 약간 높으므로 채소와 단백질 위주로 추천합니다."
            else:
                reason += " 현재 BMI가 높은 편이므로 칼로리를 줄이고 채소와 건강한 단백질 위주로 추천합니다."
        
        # 응답 생성
        response = {
            "recommendation": recommendation,
            "reason": reason,
            "timestamp": datetime.datetime.now().isoformat()
        }
        
        logger.info(f"응답 전송: {response}")
        return jsonify(response)
        
    except Exception as e:
        logger.error(f"오류 발생: {str(e)}")
        return jsonify({
            "error": "추천 처리 중 오류가 발생했습니다.",
            "message": str(e)
        }), 500

def save_request_log(request_data):
    """요청 데이터를 로그 파일에 저장"""
    try:
        with open(LOG_FILE, 'a') as f:
            log_entry = {
                "timestamp": datetime.datetime.now().isoformat(),
                "data": request_data
            }
            f.write(json.dumps(log_entry) + "\n")
    except Exception as e:
        logger.error(f"로그 저장 중 오류: {str(e)}")

if __name__ == '__main__':
    print("===== 식단 추천 시스템 서버 시작 =====")
    print("개발자용 서버: http://localhost:5001")
    print("실제 추천은 로컬 머신에서 처리됩니다. 개발 목적으로만 사용하세요.")
    print("요청 로그는 recommendation_requests.log 파일에 저장됩니다.")
    app.run(host='0.0.0.0', port=5001, debug=True)  # 포트를 5001로 변경 