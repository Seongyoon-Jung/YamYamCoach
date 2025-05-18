from flask import Flask, request, jsonify
import json
import logging
import datetime
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # CORS 활성화 - 브라우저에서 직접 API 호출 가능

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 수신 데이터 로그 저장 경로
LOG_FILE = "recommendation_requests.log"

@app.route('/recommend', methods=['POST'])
def recommend_dinner():
    """
    저녁 메뉴 추천 API
    
    입력:
    - user: 사용자 정보 (성별, 나이, 키, 몸무게 등)
    - nutrients: 오늘 섭취한 영양소 정보 (칼로리, 단백질, 탄수화물, 지방, 당)
    - mealCount: 오늘 섭취한 식사 수
    - dishIds: 오늘 섭취한 음식 ID 목록
    
    출력:
    - recommendation: 추천 메뉴
    - reason: 추천 이유
    """
    try:
        # 요청 데이터 파싱
        request_data = request.get_json()
        logger.info(f"수신된 요청: {request_data}")
        
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