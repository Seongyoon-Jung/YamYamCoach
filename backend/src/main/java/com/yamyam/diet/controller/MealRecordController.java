package com.yamyam.diet.controller;

import com.yamyam.diet.dto.MealRecordDto;
import com.yamyam.diet.entity.MealRecord;
import com.yamyam.diet.service.MealRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/extra-meal-records")
public class MealRecordController {

    private final MealRecordService mealRecordService;

    @Autowired
    public MealRecordController(MealRecordService mealRecordService) {
        this.mealRecordService = mealRecordService;
    }

    // 식사 기록 생성
    @PostMapping
    public ResponseEntity<MealRecordDto> createMealRecord(@RequestBody MealRecordDto mealRecordDto) {
        // 현재는 테스트를 위해 userId를 1로 고정
        if (mealRecordDto.getUserId() == null) {
            mealRecordDto.setUserId(1L);
        }
        
        MealRecord savedRecord = mealRecordService.saveMealRecord(mealRecordDto.toEntity());
        return new ResponseEntity<>(MealRecordDto.fromEntity(savedRecord), HttpStatus.CREATED);
    }

    // 날짜별 식사 기록 조회
    @GetMapping("/by-date/{date}")
    public ResponseEntity<List<MealRecordDto>> getMealRecordsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<MealRecord> mealRecords = mealRecordService.getMealRecordsByDate(date);
        List<MealRecordDto> mealRecordDtos = mealRecords.stream()
                .map(MealRecordDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealRecordDtos);
    }

    // 사용자 및 날짜별 식사 기록 조회
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<MealRecordDto>> getMealRecordsByUserAndDate(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<MealRecord> mealRecords = mealRecordService.getMealRecordsByUserAndDate(userId, date);
        List<MealRecordDto> mealRecordDtos = mealRecords.stream()
                .map(MealRecordDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealRecordDtos);
    }

    // 사용자, 날짜 및 타입별 식사 기록 조회
    @GetMapping("/user/{userId}/date/{date}/type/{type}")
    public ResponseEntity<List<MealRecordDto>> getMealRecordsByUserDateAndType(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String type) {
        MealRecord.CourseType courseType = MealRecord.CourseType.valueOf(type.toUpperCase());
        List<MealRecord> mealRecords = mealRecordService.getMealRecordsByUserDateAndType(userId, date, courseType);
        List<MealRecordDto> mealRecordDtos = mealRecords.stream()
                .map(MealRecordDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealRecordDtos);
    }

    // 식사 기록 수정
    @PutMapping("/{id}")
    public ResponseEntity<MealRecordDto> updateMealRecord(
            @PathVariable Long id,
            @RequestBody MealRecordDto mealRecordDto) {
        try {
            MealRecord updatedRecord = mealRecordService.updateMealRecord(id, mealRecordDto.toEntity());
            return ResponseEntity.ok(MealRecordDto.fromEntity(updatedRecord));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 식사 기록 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealRecord(@PathVariable Long id) {
        try {
            mealRecordService.deleteMealRecord(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 사용자 및 날짜 범위별 식사 기록 조회
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<MealRecordDto>> getMealRecordsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MealRecord> mealRecords = mealRecordService.getMealRecordsByUserAndDateRange(userId, startDate, endDate);
        List<MealRecordDto> mealRecordDtos = mealRecords.stream()
                .map(MealRecordDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealRecordDtos);
    }
} 