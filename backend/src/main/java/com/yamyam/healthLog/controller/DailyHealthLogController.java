package com.yamyam.healthLog.controller;

import com.yamyam.auth.dto.SecurityAccount;
import com.yamyam.healthLog.entity.DailyHealthLog;
import com.yamyam.healthLog.service.DailyHealthLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health-log")
public class DailyHealthLogController {
    
    private static final Logger logger = LoggerFactory.getLogger(DailyHealthLogController.class);
    
    @Autowired
    private DailyHealthLogService dailyHealthLogService;
    
    /**
     * 오늘의 건강 로그 조회
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodayLog(@AuthenticationPrincipal SecurityAccount principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        
        try {
            int userId = principal.getUserId();
            DailyHealthLog healthLog = dailyHealthLogService.getTodayHealthLog(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", healthLog);
            
            logger.info("사용자 ID [{}]의 오늘 건강 로그 조회 성공", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("건강 로그 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "건강 로그 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 운동 시간 업데이트 (증가/감소)
     */
    @PostMapping("/exercise")
    public ResponseEntity<?> updateExercise(
            @AuthenticationPrincipal SecurityAccount principal,
            @RequestParam("minutes") Integer minutes) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        
        try {
            int userId = principal.getUserId();
            DailyHealthLog healthLog = dailyHealthLogService.updateExerciseMinutes(userId, minutes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", healthLog);
            
            logger.info("사용자 ID [{}]의 운동 시간 업데이트 성공 ({}분)", userId, minutes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("운동 시간 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "운동 시간 업데이트 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 물 섭취량 업데이트 (증가/감소)
     */
    @PostMapping("/water")
    public ResponseEntity<?> updateWaterIntake(
            @AuthenticationPrincipal SecurityAccount principal,
            @RequestParam("milliliters") Integer milliliters) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        
        try {
            int userId = principal.getUserId();
            DailyHealthLog healthLog = dailyHealthLogService.updateWaterIntake(userId, milliliters);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", healthLog);
            
            logger.info("사용자 ID [{}]의 물 섭취량 업데이트 성공 ({}ml)", userId, milliliters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("물 섭취량 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "물 섭취량 업데이트 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 수면 시간 업데이트
     */
    @PostMapping("/sleep")
    public ResponseEntity<?> updateSleep(
            @AuthenticationPrincipal SecurityAccount principal,
            @RequestParam("minutes") Integer minutes) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        
        try {
            int userId = principal.getUserId();
            DailyHealthLog healthLog = dailyHealthLogService.updateSleepMinutes(userId, minutes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", healthLog);
            
            logger.info("사용자 ID [{}]의 수면 시간 업데이트 성공 ({}분)", userId, minutes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("수면 시간 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "수면 시간 업데이트 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 사용자의 건강 통계 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(
            @AuthenticationPrincipal SecurityAccount principal,
            @RequestParam(value = "days", defaultValue = "7") Integer days) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증되지 않은 사용자입니다."));
        }
        
        try {
            int userId = principal.getUserId();
            Map<String, Object> stats = dailyHealthLogService.getUserHealthStats(userId, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            logger.info("사용자 ID [{}]의 건강 통계 조회 성공 ({}일)", userId, days);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("건강 통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "건강 통계 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 