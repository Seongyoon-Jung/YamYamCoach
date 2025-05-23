package com.yamyam.diet.repository;

import com.yamyam.diet.entity.DishRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DishRecordRepository extends JpaRepository<DishRecord, Integer> {
    // 오늘의 기록 조회
    List<DishRecord> findByUserIdAndRecordedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end);
    
    // 중복 체크를 위한 메서드 추가
    List<DishRecord> findByUserIdAndScheduleIdIn(Integer userId, List<Integer> scheduleIds);
    
    // 오늘의 기록 삭제
    @Modifying
    void deleteByUserIdAndRecordedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end);
    
    // 특정 음식 기록 조회
    List<DishRecord> findByUserIdAndScheduleIdAndDishId(Integer userId, Integer scheduleId, Integer dishId);
    
    // 특정 음식 기록 삭제
    @Modifying
    void deleteByUserIdAndScheduleIdAndDishId(Integer userId, Integer scheduleId, Integer dishId);
    
    // 사용자의 모든 오늘 기록 삭제 (네이티브 쿼리 사용)
    @Modifying
    @Query(value = "DELETE FROM dish_record WHERE user_id = :userId AND DATE(recorded_at) = CURRENT_DATE()", nativeQuery = true)
    void deleteAllTodayRecordsByUser(@Param("userId") Integer userId);

    // 특정 스케줄의 모든 기록 조회
    List<DishRecord> findByUserIdAndScheduleId(Integer userId, Integer scheduleId);

    // 특정 스케줄의 모든 기록 삭제
    @Modifying
    void deleteByUserIdAndScheduleId(Integer userId, Integer scheduleId);

    // 사용자의 가장 최근 recordedAt 시간 조회
    @Query("SELECT MAX(dr.recordedAt) FROM DishRecord dr WHERE dr.userId = :userId")
    Optional<LocalDateTime> findLatestRecordedAtByUserId(@Param("userId") Integer userId);

    // 특정 사용자의 특정 recordedAt 시간에 해당하는 모든 DishRecord 조회
    List<DishRecord> findByUserIdAndRecordedAt(Integer userId, LocalDateTime recordedAt);
}