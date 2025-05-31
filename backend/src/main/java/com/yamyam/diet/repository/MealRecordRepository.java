package com.yamyam.diet.repository;

import com.yamyam.diet.entity.MealRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {
    
    // 특정 날짜의 식사 기록 조회
    List<MealRecord> findByRecordedAt(LocalDate recordedAt);
    
    // 특정 사용자의 특정 날짜 식사 기록 조회
    List<MealRecord> findByUserIdAndRecordedAt(Long userId, LocalDate recordedAt);
    
    // 특정 사용자의 특정 날짜, 특정 타입 식사 기록 조회
    List<MealRecord> findByUserIdAndRecordedAtAndCourseType(Long userId, LocalDate recordedAt, MealRecord.CourseType courseType);
    
    // 특정 날짜 범위의 식사 기록 조회
    List<MealRecord> findByUserIdAndRecordedAtBetween(Long userId, LocalDate startDate, LocalDate endDate);
} 