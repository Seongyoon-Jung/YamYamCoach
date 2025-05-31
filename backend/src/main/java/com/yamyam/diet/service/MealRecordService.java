package com.yamyam.diet.service;

import com.yamyam.diet.entity.MealRecord;
import com.yamyam.diet.repository.MealRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;

    @Autowired
    public MealRecordService(MealRecordRepository mealRecordRepository) {
        this.mealRecordRepository = mealRecordRepository;
    }

    // 식사 기록 저장
    @Transactional
    public MealRecord saveMealRecord(MealRecord mealRecord) {
        return mealRecordRepository.save(mealRecord);
    }

    // 특정 날짜의 식사 기록 조회
    @Transactional(readOnly = true)
    public List<MealRecord> getMealRecordsByDate(LocalDate date) {
        return mealRecordRepository.findByRecordedAt(date);
    }

    // 특정 사용자의 특정 날짜 식사 기록 조회
    @Transactional(readOnly = true)
    public List<MealRecord> getMealRecordsByUserAndDate(Long userId, LocalDate date) {
        return mealRecordRepository.findByUserIdAndRecordedAt(userId, date);
    }

    // 특정 사용자의 특정 날짜, 특정 타입 식사 기록 조회
    @Transactional(readOnly = true)
    public List<MealRecord> getMealRecordsByUserDateAndType(Long userId, LocalDate date, MealRecord.CourseType courseType) {
        return mealRecordRepository.findByUserIdAndRecordedAtAndCourseType(userId, date, courseType);
    }

    // 식사 기록 수정
    @Transactional
    public MealRecord updateMealRecord(Long id, MealRecord updatedRecord) {
        Optional<MealRecord> existingRecord = mealRecordRepository.findById(id);
        
        if (existingRecord.isPresent()) {
            MealRecord record = existingRecord.get();
            record.setDishName(updatedRecord.getDishName());
            record.setCalories(updatedRecord.getCalories());
            record.setCarbs(updatedRecord.getCarbs());
            record.setProtein(updatedRecord.getProtein());
            record.setFat(updatedRecord.getFat());
            record.setSugar(updatedRecord.getSugar());
            record.setCourseType(updatedRecord.getCourseType());
            
            return mealRecordRepository.save(record);
        } else {
            throw new IllegalArgumentException("해당 ID의 식사 기록을 찾을 수 없습니다: " + id);
        }
    }

    // 식사 기록 삭제
    @Transactional
    public void deleteMealRecord(Long id) {
        mealRecordRepository.deleteById(id);
    }

    // 특정 사용자의 특정 날짜 범위 식사 기록 조회
    @Transactional(readOnly = true)
    public List<MealRecord> getMealRecordsByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealRecordRepository.findByUserIdAndRecordedAtBetween(userId, startDate, endDate);
    }
} 