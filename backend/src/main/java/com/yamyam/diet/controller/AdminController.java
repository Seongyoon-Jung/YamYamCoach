package com.yamyam.diet.controller;

import com.yamyam.diet.entity.CourseSchedule;
import com.yamyam.diet.entity.Dish;
import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.entity.ScheduleDish;
import com.yamyam.diet.repository.CourseScheduleRepository;
import com.yamyam.diet.repository.DishRecordRepository;
import com.yamyam.diet.repository.ScheduleDishRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;
    
    @Autowired
    private ScheduleDishRepository scheduleDishRepository;
    
    @Autowired
    private DishRecordRepository dishRecordRepository;
    
    /**
     * 테스트 목적으로 특정 사용자의 지난 N일 동안의 식단 기록을 생성합니다.
     * 각 날짜마다 A 또는 B 코스에서 무작위로 음식을 선택합니다.
     */
    @PostMapping("/generate-diet-history")
    @Transactional
    public ResponseEntity<?> generateUserDietHistory(@RequestBody Map<String, Object> request) {
        try {
            // 요청에서 파라미터 추출
            Integer userId = (Integer) request.get("userId");
            Integer days = (Integer) request.get("days");
            
            if (userId == null || days == null || days <= 0 || days > 30) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "유효하지 않은 매개변수입니다. userId와 days(1-30)는 필수입니다."));
            }
            
            logger.info("사용자 ID [{}]의 지난 {}일간 식단 기록 생성 시작", userId, days);
            
            // 기존 기록 삭제
            LocalDateTime startDate = LocalDate.now().minusDays(days).atStartOfDay();
            LocalDateTime endDate = LocalDate.now().minusDays(1).atTime(23, 59, 59);
            
            dishRecordRepository.deleteByUserIdAndRecordedAtBetween(userId, startDate, endDate);
            logger.info("사용자 ID [{}]의 기존 기록 삭제 완료", userId);
            
            // 새 기록 생성
            int totalRecords = 0;
            Random random = new Random();
            
            for (int i = 1; i <= days; i++) {
                LocalDate date = LocalDate.now().minusDays(i);
                
                // 각 날짜에 대한 A 또는 B 코스 중 하나 선택
                char courseType = random.nextBoolean() ? 'A' : 'B';
                
                // 해당 날짜와 코스 타입의 코스 스케줄 조회
                Optional<CourseSchedule> courseScheduleOpt = courseScheduleRepository.findByScheduleDateAndCourseType(date, String.valueOf(courseType));
                
                if (courseScheduleOpt.isPresent()) {
                    CourseSchedule courseSchedule = courseScheduleOpt.get();
                    int scheduleId = courseSchedule.getScheduleId();
                    
                    // 해당 스케줄의 모든 음식 조회
                    List<ScheduleDish> dishes = scheduleDishRepository.findByIdScheduleId(scheduleId);
                    
                    // 음식 중 일부만 랜덤하게 선택 (40-80%)
                    int numToSelect = Math.max(1, random.nextInt((int)(dishes.size() * 0.4)) + (int)(dishes.size() * 0.4));
                    Collections.shuffle(new ArrayList<>(dishes));
                    
                    // 선택된 음식들에 대한 기록 생성
                    for (int j = 0; j < numToSelect && j < dishes.size(); j++) {
                        ScheduleDish dish = dishes.get(j);
                        
                        DishRecord record = new DishRecord();
                        record.setUserId(userId);
                        record.setScheduleId(scheduleId);
                        record.setDishId(dish.getDish().getDishId());
                        record.setCourseType(String.valueOf(courseType));
                        record.setRecordedAt(date.atTime(random.nextInt(12) + 8, random.nextInt(60))); // 8AM-8PM 사이 랜덤 시간
                        
                        dishRecordRepository.save(record);
                        totalRecords++;
                    }
                    
                    logger.info("날짜 [{}]에 대한 {}개의 음식 기록 생성 완료", date, numToSelect);
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", String.format("사용자 ID %d의 지난 %d일간 식단 기록 %d개가 생성되었습니다.", userId, days, totalRecords),
                "totalRecords", totalRecords
            ));
            
        } catch (Exception e) {
            logger.error("식단 기록 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "식단 기록 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 