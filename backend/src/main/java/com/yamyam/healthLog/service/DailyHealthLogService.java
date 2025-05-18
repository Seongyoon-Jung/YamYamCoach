package com.yamyam.healthLog.service;

import com.yamyam.healthLog.entity.DailyHealthLog;
import com.yamyam.healthLog.repository.DailyHealthLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DailyHealthLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(DailyHealthLogService.class);
    
    @Autowired
    private DailyHealthLogRepository dailyHealthLogRepository;
    
    /**
     * 오늘의 건강 로그를 조회하거나 없으면 생성합니다.
     */
    @Transactional
    public DailyHealthLog getTodayHealthLog(Integer userId) {
        LocalDate today = LocalDate.now();
        return getUserHealthLogByDate(userId, today);
    }
    
    /**
     * 특정 날짜의 건강 로그를 조회하거나 없으면 생성합니다.
     */
    @Transactional
    public DailyHealthLog getUserHealthLogByDate(Integer userId, LocalDate date) {
        Optional<DailyHealthLog> existingLog = dailyHealthLogRepository.findByUserIdAndLogDate(userId, date);
        
        if (existingLog.isPresent()) {
            return existingLog.get();
        } else {
            // 로그가 없으면 새로 생성
            DailyHealthLog newLog = new DailyHealthLog(userId, date);
            return dailyHealthLogRepository.save(newLog);
        }
    }
    
    /**
     * 운동 시간을 업데이트합니다.
     */
    @Transactional
    public DailyHealthLog updateExerciseMinutes(Integer userId, Integer minutes) {
        DailyHealthLog healthLog = getTodayHealthLog(userId);
        
        // 운동 시간 조정 (음수가 되지 않도록)
        int newExerciseMinutes = Math.max(0, healthLog.getExerciseMinutes() + minutes);
        healthLog.setExerciseMinutes(newExerciseMinutes);
        
        logger.info("사용자 ID [{}]의 운동 시간 업데이트: {} → {}", userId, healthLog.getExerciseMinutes() - minutes, newExerciseMinutes);
        return dailyHealthLogRepository.save(healthLog);
    }
    
    /**
     * 물 섭취량을 업데이트합니다.
     */
    @Transactional
    public DailyHealthLog updateWaterIntake(Integer userId, Integer milliliters) {
        DailyHealthLog healthLog = getTodayHealthLog(userId);
        
        // 물 섭취량 조정 (음수가 되지 않도록)
        int newWaterIntake = Math.max(0, healthLog.getWaterIntakeMl() + milliliters);
        healthLog.setWaterIntakeMl(newWaterIntake);
        
        logger.info("사용자 ID [{}]의 물 섭취량 업데이트: {} → {}", userId, healthLog.getWaterIntakeMl() - milliliters, newWaterIntake);
        return dailyHealthLogRepository.save(healthLog);
    }
    
    /**
     * 수면 시간을 업데이트합니다.
     */
    @Transactional
    public DailyHealthLog updateSleepMinutes(Integer userId, Integer minutes) {
        DailyHealthLog healthLog = getTodayHealthLog(userId);
        
        // 수면 시간 조정 (음수가 되지 않도록)
        int newSleepMinutes = Math.max(0, healthLog.getSleepMinutes() + minutes);
        healthLog.setSleepMinutes(newSleepMinutes);
        
        logger.info("사용자 ID [{}]의 수면 시간 업데이트: {} → {}", userId, healthLog.getSleepMinutes() - minutes, newSleepMinutes);
        return dailyHealthLogRepository.save(healthLog);
    }
    
    /**
     * 사용자의 최근 n일간 건강 로그 통계를 조회합니다.
     */
    public Map<String, Object> getUserHealthStats(Integer userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        List<DailyHealthLog> logs = dailyHealthLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(
                userId, startDate, endDate);
        
        // 통계 계산
        int totalExerciseMinutes = 0;
        int totalWaterIntakeMl = 0;
        int totalSleepMinutes = 0;
        
        for (DailyHealthLog log : logs) {
            totalExerciseMinutes += log.getExerciseMinutes();
            totalWaterIntakeMl += log.getWaterIntakeMl();
            totalSleepMinutes += log.getSleepMinutes();
        }
        
        // 평균 계산 (실제 로그가 있는 날짜 수 기준)
        int logDays = logs.size();
        double avgExerciseMinutes = logDays > 0 ? (double) totalExerciseMinutes / logDays : 0;
        double avgWaterIntakeMl = logDays > 0 ? (double) totalWaterIntakeMl / logDays : 0;
        double avgSleepMinutes = logDays > 0 ? (double) totalSleepMinutes / logDays : 0;
        
        // 결과 맵 생성
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDays", logDays);
        stats.put("totalExerciseMinutes", totalExerciseMinutes);
        stats.put("totalWaterIntakeMl", totalWaterIntakeMl);
        stats.put("totalSleepMinutes", totalSleepMinutes);
        stats.put("avgExerciseMinutes", avgExerciseMinutes);
        stats.put("avgWaterIntakeMl", avgWaterIntakeMl);
        stats.put("avgSleepMinutes", avgSleepMinutes);
        stats.put("logs", logs);
        
        return stats;
    }
} 