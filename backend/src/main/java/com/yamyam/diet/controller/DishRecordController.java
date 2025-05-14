package com.yamyam.diet.controller;

import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.service.DishRecordService;
import com.yamyam.dto.SecurityAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/meal-records")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DishRecordController {

    @Autowired
    private DishRecordService dishRecordService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveMealRecords(
            @AuthenticationPrincipal SecurityAccount principal, 
            @RequestBody MealRecordRequest req) {
        try {
            int userId = principal.getUserId();
            List<DishRecord> records = new ArrayList<>();
            
            // 요청 데이터 디버깅
            System.out.println("요청 데이터: courseType=" + req.getCourseType() + ", meals=" + req.getMeals());
            
            if (req.getMeals() == null || req.getMeals().isEmpty()) {
                throw new RuntimeException("선택된 음식이 없습니다.");
            }
            
            for (Map<String, Object> m : req.getMeals()) {
                // 값 확인 및 안전한 변환
                Integer scheduleId = null;
                Integer dishId = null;
                
                // schedule_id 안전한 변환
                if (m.get("schedule_id") != null) {
                    if (m.get("schedule_id") instanceof Integer) {
                        scheduleId = (Integer) m.get("schedule_id");
                    } else if (m.get("schedule_id") instanceof Number) {
                        scheduleId = ((Number) m.get("schedule_id")).intValue();
                    } else if (m.get("schedule_id") instanceof String) {
                        try {
                            scheduleId = Integer.parseInt((String) m.get("schedule_id"));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("유효하지 않은 schedule_id 형식: " + m.get("schedule_id"));
                        }
                    }
                }
                
                // dish_id 안전한 변환
                if (m.get("dish_id") != null) {
                    if (m.get("dish_id") instanceof Integer) {
                        dishId = (Integer) m.get("dish_id");
                    } else if (m.get("dish_id") instanceof Number) {
                        dishId = ((Number) m.get("dish_id")).intValue();
                    } else if (m.get("dish_id") instanceof String) {
                        try {
                            dishId = Integer.parseInt((String) m.get("dish_id"));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("유효하지 않은 dish_id 형식: " + m.get("dish_id"));
                        }
                    }
                }
                
                // null 체크
                if (scheduleId == null) {
                    throw new RuntimeException("schedule_id가 누락되었습니다. 데이터: " + m);
                }
                if (dishId == null) {
                    throw new RuntimeException("dish_id가 누락되었습니다. 데이터: " + m);
                }
                
                DishRecord dr = new DishRecord();
                dr.setUserId(userId);
                dr.setScheduleId(scheduleId);
                dr.setDishId(dishId);
                dr.setCourseType(req.getCourseType());
                dr.setRecordedAt(LocalDateTime.now());
                records.add(dr);
                
                System.out.println("생성된 레코드: " + dr);
            }
            
            dishRecordService.saveRecords(userId, req.getCourseType(), records);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "식단이 성공적으로 저장되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 서버 콘솔에 상세 오류 출력
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 내부 DTO 클래스
    public static class MealRecordRequest {
        private String courseType;
        private List<Map<String, Object>> meals;

        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }
        public List<Map<String, Object>> getMeals() { return meals; }
        public void setMeals(List<Map<String, Object>> meals) { this.meals = meals; }
    }
}