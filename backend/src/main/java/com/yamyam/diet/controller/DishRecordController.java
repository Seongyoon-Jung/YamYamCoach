package com.yamyam.diet.controller;

import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.service.DishRecordService;
import com.yamyam.dto.SecurityAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meal-records")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 실제 운영 환경에서는 특정 도메인 지정
public class DishRecordController {

    private static final Logger logger = LoggerFactory.getLogger(DishRecordController.class);

    @Autowired
    private DishRecordService dishRecordService;

    // DTO 클래스는 외부에 정의하거나 static inner class로 유지
    public static class MealRecordRequest {
        private String courseType;
        private List<Map<String, Object>> meals; // 각 Map은 { "schedule_id": Long, "dish_id": Long } 형태

        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }
        public List<Map<String, Object>> getMeals() { return meals; }
        public void setMeals(List<Map<String, Object>> meals) { this.meals = meals; }
        // 로깅 또는 디버깅을 위한 toString 메소드 추가
        @Override
        public String toString() {
            return "MealRecordRequest{" +
                   "courseType='" + courseType + '\'' +
                   ", meals=" + (meals != null ? meals.size() + " items" : "null") +
                   '}';
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayRecords(@AuthenticationPrincipal SecurityAccount principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        try {
            int userId = principal.getUserId();
            List<DishRecord> records = dishRecordService.getTodayRecords(userId);
            List<Map<String, Object>> result = records.stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("recordId", record.getRecordId());
                    map.put("userId", record.getUserId());
                    map.put("scheduleId", record.getScheduleId());
                    map.put("dishId", record.getDishId()); // 프론트엔드에서 dish_id 대신 dishId를 사용하고 있다면 통일 필요
                    map.put("courseType", record.getCourseType());
                    map.put("recordedAt", record.getRecordedAt());
                    return map;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("오늘의 식단 기록 조회 중 오류 발생 (사용자 ID: {})", principal.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "기록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    // 신규 저장 엔드포인트
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMealRecords(
            @AuthenticationPrincipal SecurityAccount principal,
            @RequestBody MealRecordRequest req) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = principal.getUserId();
            logger.info("식단 신규 저장 요청: 사용자 ID [{}], 요청 데이터 [{}]", userId, req);

            if (req.getCourseType() == null || req.getCourseType().isEmpty()) {
                 response.put("success", false);
                 response.put("message", "courseType은 필수입니다.");
                 return ResponseEntity.badRequest().body(response);
            }
            // meals가 null이거나 비어있으면 안됨 (신규 저장이므로)
            if (req.getMeals() == null || req.getMeals().isEmpty()) {
                response.put("success", false);
                response.put("message", "선택된 음식이 없습니다. 최소 하나 이상의 음식을 선택해야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }

            List<DishRecord> recordsToSave = convertMealRequestToDishRecords(req, userId);
            
            dishRecordService.createNewMealRecords(userId, req.getCourseType(), recordsToSave);

            response.put("success", true);
            response.put("message", "식단이 성공적으로 저장되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("식단 신규 저장 요청 처리 중 유효하지 않은 인자: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
             logger.warn("식단 신규 저장 요청 처리 중 상태 오류: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
        } 
        catch (Exception e) {
            logger.error("식단 신규 저장 중 서버 오류 발생 (사용자 ID: {})", principal.getUserId(), e);
            response.put("success", false);
            response.put("message", "식단 저장 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 수정 엔드포인트
    @PostMapping("/update") // 또는 @PutMapping 사용 고려
    public ResponseEntity<Map<String, Object>> updateMealRecords(
            @AuthenticationPrincipal SecurityAccount principal,
            @RequestBody MealRecordRequest req) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = principal.getUserId();
            logger.info("식단 수정 요청: 사용자 ID [{}], 요청 데이터 [{}]", userId, req);
            
            if (req.getCourseType() == null || req.getCourseType().isEmpty()) {
                 response.put("success", false);
                 response.put("message", "courseType은 필수입니다.");
                 return ResponseEntity.badRequest().body(response);
            }
            // meals가 null인 경우는 허용 (빈 리스트로 간주하여 모든 항목 삭제)
            List<DishRecord> recordsToSaveOrUpdate;
            if (req.getMeals() == null) {
                recordsToSaveOrUpdate = new ArrayList<>(); // 빈 리스트로 만들어 모든 기록 삭제 유도
            } else {
                recordsToSaveOrUpdate = convertMealRequestToDishRecords(req, userId);
            }
            
            dishRecordService.replaceMealRecords(userId, req.getCourseType(), recordsToSaveOrUpdate);

            response.put("success", true);
            response.put("message", "식단이 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("식단 수정 요청 처리 중 유효하지 않은 인자: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("식단 수정 중 서버 오류 발생 (사용자 ID: {})", principal.getUserId(), e);
            response.put("success", false);
            response.put("message", "식단 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper method to convert request DTO to list of DishRecord entities
    private List<DishRecord> convertMealRequestToDishRecords(MealRecordRequest req, int userId) {
        List<DishRecord> records = new ArrayList<>();
        if (req.getMeals() != null) {
            for (Map<String, Object> mealMap : req.getMeals()) {
                Integer scheduleId = safeCastToInteger(mealMap.get("schedule_id"), "schedule_id");
                Integer dishId = safeCastToInteger(mealMap.get("dish_id"), "dish_id");

                if (scheduleId == null) {
                    throw new IllegalArgumentException("각 meal 항목에는 schedule_id가 반드시 포함되어야 합니다. 누락된 항목: " + mealMap);
                }
                if (dishId == null) {
                    throw new IllegalArgumentException("각 meal 항목에는 dish_id가 반드시 포함되어야 합니다. 누락된 항목: " + mealMap);
                }

                DishRecord dr = new DishRecord();
                dr.setUserId(userId);
                dr.setScheduleId(scheduleId);
                dr.setDishId(dishId);
                dr.setCourseType(req.getCourseType()); // courseType은 request의 최상위 레벨에서 가져옴
                dr.setRecordedAt(LocalDateTime.now()); // 저장/수정 시 항상 현재 시간으로
                records.add(dr);
            }
        }
        return records;
    }
    
    private Integer safeCastToInteger(Object value, String fieldName) {
        if (value == null) return null;
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("유효하지 않은 " + fieldName + " 형식입니다: '" + value + "'. 숫자여야 합니다.");
            }
        }
        throw new IllegalArgumentException("지원하지 않는 " + fieldName + " 타입입니다: " + value.getClass().getName() + " (값: '" + value + "')");
    }

    // 5일간의 식단 기록 조회 엔드포인트 추가
    @GetMapping("/last-5-days")
    public ResponseEntity<?> getLast5DaysRecords(@AuthenticationPrincipal SecurityAccount principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        try {
            int userId = principal.getUserId();
            List<Map<String, Object>> records = dishRecordService.getLast5DaysRecords(userId);
            logger.info("사용자 ID [{}]의 5일간 식단 영양소 기록 조회 완료: {} 일치", userId, records.size());
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            logger.error("5일간 식단 기록 조회 중 오류 발생 (사용자 ID: {})", principal.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "기록 조회 중 오류 발생: " + e.getMessage()));
        }
    }

    // toggle 및 cleanup 엔드포인트는 현재 프론트엔드 사용 방식과 맞지 않거나 불필요해 보이므로 검토 후 제거 또는 수정
    // @PostMapping("/toggle") ...
    // @PostMapping("/cleanup") ...
    // DishToggleRequest 클래스는 현재 사용되지 않으므로 제거하거나 주석 처리 가능
    /*
    public static class DishToggleRequest {
        private String courseType;
        private Integer schedule_id;
        private Integer dish_id;
        private boolean checked;

        // Getters and Setters
        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }
        public Integer getSchedule_id() { return schedule_id; }
        public void setSchedule_id(Integer schedule_id) { this.schedule_id = schedule_id; }
        public Integer getDish_id() { return dish_id; }
        public void setDish_id(Integer dish_id) { this.dish_id = dish_id; }
        public boolean isChecked() { return checked; }
        public void setChecked(boolean checked) { this.checked = checked; }
    }
    */
}