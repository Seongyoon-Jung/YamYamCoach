package com.yamyam.diet.service;

import com.yamyam.dto.request.DishRecordRequest;
import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.repository.DishRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishRecordService {

    @Autowired
    private DishRecordRepository dishRecordRepository;

    @Transactional
    public void saveRecords(Integer userId, String courseType, List<DishRecord> records) {
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("선택된 음식이 없습니다.");
        }

        // 기존 기록 삭제
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        dishRecordRepository.deleteByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);

        // 새로운 기록 저장
        for (DishRecord record : records) {
            record.setUserId(userId);
            record.setCourseType(courseType);
            record.setRecordedAt(LocalDateTime.now());
        }
        dishRecordRepository.saveAll(records);
    }

    @Transactional
    public void updateTodayRecords(Integer userId, String courseType, List<DishRecord> records) {
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("선택된 음식이 없습니다.");
        }

        // 기존 기록 삭제
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        dishRecordRepository.deleteByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);

        // 새로운 기록 저장
        for (DishRecord record : records) {
            record.setUserId(userId);
            record.setCourseType(courseType);
            record.setRecordedAt(LocalDateTime.now());
        }
        dishRecordRepository.saveAll(records);
    }

    public List<DishRecord> getTodayRecords(Integer userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);
    }
}