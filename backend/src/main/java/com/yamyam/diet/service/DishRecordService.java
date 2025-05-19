package com.yamyam.diet.service;

import com.yamyam.dto.request.DishRecordRequest;
import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.repository.DishRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DishRecordService {

    @Autowired
    private DishRecordRepository dishRecordRepository;

    /**
     * 특정 사용자의 오늘 모든 기록을 삭제합니다.
     */
    @Transactional
    public void deleteTodayRecords(Integer userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // 삭제할 레코드 먼저 조회
        List<DishRecord> recordsToDelete = dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);
        
        System.out.println("===== 삭제 예정 레코드 =====");
        for (DishRecord record : recordsToDelete) {
            System.out.println("삭제 예정: " + record);
        }
        
        if (!recordsToDelete.isEmpty()) {
            try {
                // 네이티브 쿼리로 먼저 시도
                dishRecordRepository.deleteAllTodayRecordsByUser(userId);
                
                // JPA 메서드로 다시 시도
                dishRecordRepository.deleteByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);
                
                // 개별 레코드 하나씩 삭제
                for (DishRecord record : recordsToDelete) {
                    try {
                        dishRecordRepository.deleteById(record.getRecordId());
                    } catch (Exception e) {
                        System.out.println("개별 레코드 삭제 중 오류: " + e.getMessage());
                    }
                }
                
                // 삭제 후 실제로 삭제되었는지 확인
                List<DishRecord> remainingRecords = dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);
                if (!remainingRecords.isEmpty()) {
                    System.out.println("경고: 삭제 후에도 " + remainingRecords.size() + "개의 레코드가 남아있습니다.");
                    for (DishRecord record : remainingRecords) {
                        System.out.println("  남은 레코드: " + record);
                    }
                } else {
                    System.out.println("모든 레코드가 성공적으로 삭제되었습니다.");
                }
            } catch (Exception e) {
                System.err.println("레코드 삭제 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("삭제할 레코드가 없습니다.");
        }
    }

    @Transactional
    public void toggleRecord(Integer userId, DishRecord record, boolean add) {
        if (add) {
            // 이미 존재하는지 확인
            List<DishRecord> existingRecords = dishRecordRepository.findByUserIdAndScheduleIdAndDishId(
                userId, record.getScheduleId(), record.getDishId());
            
            if (existingRecords.isEmpty()) {
                // 존재하지 않으면 추가
                record.setUserId(userId);
                record.setRecordedAt(LocalDateTime.now());
                dishRecordRepository.save(record);
            }
            // 이미 존재하면 무시
        } else {
            // 체크 해제된 경우 레코드 삭제
            dishRecordRepository.deleteByUserIdAndScheduleIdAndDishId(
                userId, record.getScheduleId(), record.getDishId());
        }
    }
    
    @Transactional
    public void toggleRecord(Integer userId, Integer scheduleId, Integer dishId, boolean add) {
        if (add) {
            // 구현은 위의 메서드에서 처리
            DishRecord record = new DishRecord();
            record.setUserId(userId);
            record.setScheduleId(scheduleId);
            record.setDishId(dishId);
            toggleRecord(userId, record, true);
        } else {
            // 체크 해제된 경우 레코드 삭제
            dishRecordRepository.deleteByUserIdAndScheduleIdAndDishId(userId, scheduleId, dishId);
        }
    }

    @Transactional
    public void saveRecords(Integer userId, String courseType, List<DishRecord> records) {
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("선택된 음식이 없습니다.");
        }

        try {
            // 기존 기록 삭제 - 중복 키 에러 방지를 위해 반드시 먼저 실행
            deleteTodayRecords(userId);
            
            // 트랜잭션이 제대로 적용되도록 잠시 대기
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("처리 중 인터럽트가 발생했습니다.", ie);
            }
            
            // 새로운 기록 저장 준비
            List<DishRecord> newRecords = new ArrayList<>();
            for (DishRecord record : records) {
                record.setUserId(userId);
                record.setCourseType(courseType);
                record.setRecordedAt(LocalDateTime.now());
                newRecords.add(record);
            }
            
            // 존재하는지 다시 확인 후 저장
            for (DishRecord record : newRecords) {
                List<DishRecord> existingRecord = dishRecordRepository.findByUserIdAndScheduleIdAndDishId(
                    userId, record.getScheduleId(), record.getDishId());
                
                if (existingRecord.isEmpty()) {
                    // 존재하지 않으면 개별적으로 저장
                    dishRecordRepository.save(record);
                    System.out.println("새 레코드 저장: " + record);
                } else {
                    System.out.println("중복 레코드 발견, 건너뜀: " + record);
                }
            }
        } catch (RuntimeException e) {
            System.err.println("레코드 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void updateTodayRecords(Integer userId, String courseType, List<DishRecord> records) {
        saveRecords(userId, courseType, records);
    }

    public List<DishRecord> getTodayRecords(Integer userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<DishRecord> records = dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, startOfDay, endOfDay);
        
        // 로그 출력 추가
        System.out.println("=== 사용자(" + userId + ")의 오늘 식사 기록 ===");
        for (DishRecord record : records) {
            System.out.println(record);
        }
        System.out.println("=== 총 " + records.size() + "개의 기록 로드됨 ===");
        
        return records;
    }
}