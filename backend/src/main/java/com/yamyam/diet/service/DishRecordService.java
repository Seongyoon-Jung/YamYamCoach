package com.yamyam.diet.service;

import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.repository.DishRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DishRecordService {

    @Autowired
    private DishRecordRepository dishRecordRepository;

    public List<DishRecord> getTodayRecords(int userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, start, end);
    }

    public void saveRecords(int userId, String courseType, List<DishRecord> records) {
        LocalDateTime now = LocalDateTime.now();
        for (DishRecord record : records) {
            record.setUserId(userId);
            record.setCourseType(courseType);
            record.setRecordedAt(now);
        }
        dishRecordRepository.saveAll(records);
    }

    @Transactional
    public void updateTodayRecords(int userId, String courseType, List<DishRecord> records) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        dishRecordRepository.deleteByUserIdAndRecordedAtBetween(userId, start, end);
        saveRecords(userId, courseType, records);
    }
}