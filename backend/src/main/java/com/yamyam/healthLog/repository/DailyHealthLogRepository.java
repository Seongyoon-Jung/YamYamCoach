package com.yamyam.healthLog.repository;

import com.yamyam.healthLog.entity.DailyHealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyHealthLogRepository extends JpaRepository<DailyHealthLog, Integer> {
    
    // 특정 사용자의 특정 날짜 로그 조회
    Optional<DailyHealthLog> findByUserIdAndLogDate(Integer userId, LocalDate logDate);
    
    // 특정 사용자의 최근 로그 n개 조회 (날짜 내림차순)
    List<DailyHealthLog> findByUserIdOrderByLogDateDesc(Integer userId);
    
    // 특정 사용자의 로그 전체 조회
    List<DailyHealthLog> findByUserId(Integer userId);
    
    // 특정 사용자의 특정 날짜 범위 로그 조회
    List<DailyHealthLog> findByUserIdAndLogDateBetweenOrderByLogDateDesc(
            Integer userId, LocalDate startDate, LocalDate endDate);
} 