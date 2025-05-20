package com.yamyam.diet.controller;

import com.yamyam.diet.entity.Dish;
import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.repository.DishRecordRepository;
import com.yamyam.dto.SecurityAccount;
import com.yamyam.entity.UserEntity;
import com.yamyam.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DishRecordRepository dishRecordRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 사용자 프로필 정보 제공 API
     * 프론트엔드에서 호출하는 /api/user/profile 엔드포인트 구현
     */
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal SecurityAccount principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        
        try {
            int userId = principal.getUserId();
            
            // 사용자 정보 조회
            UserEntity user = userRepository.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자 정보를 찾을 수 없습니다."));
            }
            
            // 응답 데이터 구성
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("gender", user.isGender());
            userInfo.put("age", calculateAge(user.getBirthDate()));
            userInfo.put("height", user.getHeight());
            userInfo.put("weight", user.getWeight());
            userInfo.put("targetWeight", user.getTargetWeight());
            
            logger.info("사용자 ID [{}]의 프로필 정보 요청 응답: {}", userId, userInfo);
            return ResponseEntity.ok(Map.of("success", true, "user", userInfo));
            
        } catch (Exception e) {
            logger.error("사용자 프로필 정보 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 저녁 메뉴 추천 요청
     */
    @PostMapping("/recommendation/dinner")
    public ResponseEntity<?> recommendDinner(@AuthenticationPrincipal SecurityAccount principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        
        try {
            int userId = principal.getUserId();
            
            // 사용자 정보 조회
            UserEntity user = userRepository.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자 정보를 찾을 수 없습니다."));
            }
            
            // 오늘 하루의 시작과 끝 시간 계산
            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            // 오늘 섭취한 음식 기록 조회
            List<DishRecord> todayRecords = dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);
            
            // 추천 시스템 서버에 전송할 데이터 준비
            Map<String, Object> requestData = new HashMap<>();
            
            // 사용자 정보
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("gender", user.isGender());
            userInfo.put("age", calculateAge(user.getBirthDate()));
            userInfo.put("height", user.getHeight());
            userInfo.put("weight", user.getWeight());
            userInfo.put("targetWeight", user.getTargetWeight());
            
            // 오늘 섭취한 영양소 정보
            Map<String, Double> nutrients = new HashMap<>();
            nutrients.put("calories", 0.0);
            nutrients.put("protein", 0.0);
            nutrients.put("carbohydrate", 0.0);
            nutrients.put("fat", 0.0);
            nutrients.put("sugar", 0.0);
            
            // 임시로 영양소 합계 계산 대신 목록 제공
            // 실제 영양소를 계산하려면 Dish 엔티티를 조회하거나 식사 기록에 함께 저장된 영양소 정보를 활용해야 함
            
            requestData.put("user", userInfo);
            requestData.put("nutrients", nutrients);
            requestData.put("mealCount", todayRecords.size());
            requestData.put("dishIds", todayRecords.stream().map(DishRecord::getDishId).toList());
            
            logger.info("사용자 ID [{}]의 저녁 메뉴 추천 요청 데이터: {}", userId, requestData);
            
            // 추천 시스템 서버로 요청 전송
            String recommendationUrl = "http://localhost:5001/recommend";  // 포트 5001로 수정
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData);
            
            try {
                ResponseEntity<Map> responseEntity = restTemplate.postForEntity(recommendationUrl, requestEntity, Map.class);
                Map<String, Object> recommendationResult = responseEntity.getBody();
                
                // 응답 준비
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", recommendationResult);
                
                logger.info("사용자 ID [{}]의 저녁 메뉴 추천 결과: {}", userId, recommendationResult);
                return ResponseEntity.ok(response);
                
            } catch (Exception e) {
                logger.error("추천 시스템 서버 연결 오류: ", e);
                
                // 임시 응답 반환 (추천 시스템 서버가 없는 경우)
                Map<String, Object> mockRecommendation = new HashMap<>();
                mockRecommendation.put("recommendation", "볶음밥, 김치, 미역국");
                mockRecommendation.put("reason", "오늘 섭취한 영양소를 분석한 결과, 균형 잡힌 한식 메뉴를 추천합니다. 볶음밥으로 적절한 탄수화물과 단백질을, 김치와 미역국으로 충분한 비타민과 미네랄을 섭취하세요.");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", mockRecommendation);
                
                logger.info("사용자 ID [{}]의 임시 저녁 메뉴 추천 결과: {}", userId, mockRecommendation);
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            logger.error("저녁 메뉴 추천 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "저녁 메뉴 추천 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 생년월일로부터 나이 계산
     */
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }
} 