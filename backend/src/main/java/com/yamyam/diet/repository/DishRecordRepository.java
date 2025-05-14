package com.yamyam.diet.repository;

import com.yamyam.diet.entity.DishRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DishRecordRepository extends JpaRepository<DishRecord, Integer> {
    // 오늘의 기록 조회
    List<DishRecord> findByUserIdAndRecordedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end);
    
    // 중복 체크를 위한 메서드 추가
    List<DishRecord> findByUserIdAndScheduleIdIn(Integer userId, List<Integer> scheduleIds);
    
    // 오늘의 기록 삭제
    void deleteByUserIdAndRecordedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end);
}