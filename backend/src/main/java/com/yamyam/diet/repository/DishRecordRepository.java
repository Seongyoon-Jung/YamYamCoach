package com.yamyam.diet.repository;

import com.yamyam.diet.entity.DishRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DishRecordRepository extends JpaRepository<DishRecord, Integer> {
    List<DishRecord> findByUserIdAndRecordedAtBetween(int userId, LocalDateTime start, LocalDateTime end);
    void deleteByUserIdAndRecordedAtBetween(int userId, LocalDateTime start, LocalDateTime end);
}