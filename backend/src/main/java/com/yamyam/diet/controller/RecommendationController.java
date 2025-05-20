package com.yamyam.diet.controller;

import com.yamyam.diet.entity.Dish;
import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.repository.DishRecordRepository;
import com.yamyam.diet.repository.DishRepository;
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
    private DishRepository dishRepository;
    
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
            
            // 오늘 섭취한 영양소 정보 실제 계산
            double totalCalories = 0.0;
            double totalProtein = 0.0;
            double totalCarbohydrate = 0.0;
            double totalFat = 0.0;
            double totalSugar = 0.0;
            
            // 식사 기록을 순회하면서 영양소 합산
            for (DishRecord record : todayRecords) {
                try {
                    // 음식 정보 조회 - DishRepository를 통해 Dish 엔티티 조회
                    Integer dishId = record.getDishId();
                    Dish dish = findDishById(dishId);
                    
                    if (dish != null) {
                        // 기본 portion은 1.0으로 설정 (실제로는 record에서 가져와야 할 수도 있음)
                        double portion = 1.0;
                        
                        // dish 엔티티의 필드명은 calorieKcal, proteinG, carbohydrateG, fatG, sugarG임
                        totalCalories += (dish.getCalorieKcal() != null ? dish.getCalorieKcal() : 0) * portion;
                        totalProtein += (dish.getProteinG() != null ? dish.getProteinG() : 0) * portion;
                        totalCarbohydrate += (dish.getCarbohydrateG() != null ? dish.getCarbohydrateG() : 0) * portion;
                        totalFat += (dish.getFatG() != null ? dish.getFatG() : 0) * portion;
                        totalSugar += (dish.getSugarG() != null ? dish.getSugarG() : 0) * portion;
                        
                        logger.debug("음식 [{}], 양 [{}], 칼로리 [{}], 단백질 [{}], 탄수화물 [{}], 지방 [{}], 당 [{}]", 
                                dish.getDishName(), portion, 
                                dish.getCalorieKcal() * portion, 
                                dish.getProteinG() * portion,
                                dish.getCarbohydrateG() * portion, 
                                dish.getFatG() * portion, 
                                dish.getSugarG() * portion);
                    }
                } catch (Exception e) {
                    // 개별 음식 처리 중 오류가 발생해도 전체 처리는 계속
                    logger.error("음식 정보 처리 중 오류: {}", e.getMessage());
                }
            }
            
            Map<String, Double> nutrients = new HashMap<>();
            nutrients.put("calories", totalCalories);
            nutrients.put("protein", totalProtein);
            nutrients.put("carbohydrate", totalCarbohydrate);
            nutrients.put("fat", totalFat);
            nutrients.put("sugar", totalSugar);
            
            logger.info("사용자 ID [{}]의 오늘 섭취한 영양소 합계: 칼로리 [{}], 단백질 [{}], 탄수화물 [{}], 지방 [{}], 당 [{}]", 
                    userId, totalCalories, totalProtein, totalCarbohydrate, totalFat, totalSugar);
            
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
    
    /**
     * 음식 ID로 Dish 엔티티 찾기
     * DishRepository를 사용하여 실제 DB에서 음식 정보 조회
     */
    private Dish findDishById(Integer dishId) {
        if (dishId == null) {
            return null;
        }
        
        Optional<Dish> dishOptional = dishRepository.findById(dishId);
        if (dishOptional.isPresent()) {
            return dishOptional.get();
        }
        
        // DB에서 찾을 수 없는 경우 로그 출력
        logger.warn("음식 ID [{}]에 해당하는 음식 정보를 찾을 수 없습니다", dishId);
        
        // 찾을 수 없는 경우 기본 음식 정보 생성
        Dish defaultDish = new Dish();
        defaultDish.setDishId(dishId);
        defaultDish.setDishName("알 수 없는 음식 " + dishId);
        defaultDish.setCalorieKcal(0.0);
        defaultDish.setProteinG(0.0);
        defaultDish.setCarbohydrateG(0.0);
        defaultDish.setFatG(0.0);
        defaultDish.setSugarG(0.0);
        return defaultDish;
    }
} 