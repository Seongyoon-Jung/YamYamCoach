# 식단 추천 서버

YamYamCoach 앱용 식단 추천 서버입니다.

## 설치 및 실행

1. 필요 패키지 설치
```bash
pip install -r requirements.txt
```

2. 서버 실행
```bash
python recommend_server.py
```

서버는 기본적으로 5000번 포트에서 실행됩니다.

## API 사용법

### 저녁 메뉴 추천 API

**엔드포인트**: `/recommend`
**메소드**: `POST`
**콘텐츠 타입**: `application/json`

#### 요청 형식
```json
{
  "user": {
    "userId": 1,
    "gender": true,  // true: 여성, false: 남성
    "age": 30,
    "height": 170,
    "weight": 65,
    "targetWeight": 60
  },
  "nutrients": {
    "calories": 1200,
    "protein": 45,
    "carbohydrate": 150,
    "fat": 40,
    "sugar": 20
  },
  "mealCount": 2,
  "dishIds": [101, 102, 103]
}
```

#### 응답 형식
```json
{
  "recommendation": "연어 스테이크와 구운 야채",
  "reason": "30-40대에게는 오메가3가 풍부한 생선과 섬유질 위주의 균형잡힌 식사를 추천합니다.",
  "timestamp": "2023-10-23T15:30:45.123456"
}
```

## 로깅

요청과 응답은 `recommendation_requests.log` 파일에 저장됩니다. 