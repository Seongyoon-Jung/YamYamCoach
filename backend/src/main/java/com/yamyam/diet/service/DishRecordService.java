package com.yamyam.diet.service;

import com.yamyam.dto.request.DishRecordRequest;
import com.yamyam.diet.entity.CourseSchedule;
import com.yamyam.diet.entity.Dish;
import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.repository.CourseScheduleRepository;
import com.yamyam.diet.repository.DishRecordRepository;
import com.yamyam.diet.repository.DishRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DishRecordService {

    private static final Logger logger = LoggerFactory.getLogger(DishRecordService.class);

    @Autowired
    private DishRecordRepository dishRecordRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private DishRepository dishRepository;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteRecordsByUserIdAndScheduleId(Integer userId, Integer scheduleId) {
        logger.info("사용자 ID [{}]의 스케줄 ID [{}]에 대한 기존 식단 기록 삭제 시도", userId, scheduleId);
        List<DishRecord> existingRecords = dishRecordRepository.findByUserIdAndScheduleId(userId, scheduleId);
        if (!existingRecords.isEmpty()) {
            dishRecordRepository.deleteByUserIdAndScheduleId(userId, scheduleId);
            logger.info("사용자 ID [{}]의 스케줄 ID [{}]에 대한 기존 식단 기록 {}건 삭제 완료", userId, scheduleId, existingRecords.size());
        } else {
            logger.info("사용자 ID [{}]의 스케줄 ID [{}]에 대해 삭제할 기존 식단 기록 없음", userId, scheduleId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTodayRecordsByUserIdAndCourseType(Integer userId, String courseType) {
        LocalDate today = LocalDate.now();
        // CourseScheduleRepository를 사용해 오늘 날짜와 코스타입으로 schedule_id를 찾습니다.
        CourseSchedule schedule = courseScheduleRepository.findByScheduleDateAndCourseType(today, courseType)
            .orElse(null); // .orElseThrow(() -> new RuntimeException("오늘의 " + courseType + " 코스 스케줄을 찾을 수 없습니다."));

        if (schedule != null) {
            logger.info("사용자 ID [{}]의 오늘 날짜 [{}] 코스 타입 [{}(scheduleId: {})]에 대한 기존 식단 기록 삭제 시도", userId, today, courseType, schedule.getScheduleId());
            List<DishRecord> existingRecords = dishRecordRepository.findByUserIdAndScheduleId(userId, schedule.getScheduleId());
            if (!existingRecords.isEmpty()) {
                dishRecordRepository.deleteByUserIdAndScheduleId(userId, schedule.getScheduleId());
                logger.info("사용자 ID [{}]의 오늘 날짜 [{}] 코스 타입 [{}(scheduleId: {})]에 대한 기존 식단 기록 {}건 삭제 완료", userId, today, courseType, schedule.getScheduleId(), existingRecords.size());
            } else {
                logger.info("사용자 ID [{}]의 오늘 날짜 [{}] 코스 타입 [{}(scheduleId: {})]에 대해 삭제할 기존 식단 기록 없음", userId, today, courseType, schedule.getScheduleId());
            }
        } else {
             logger.warn("사용자 ID [{}]의 오늘 날짜 [{}] 코스 타입 [{}]에 해당하는 스케줄을 찾을 수 없어 삭제를 진행하지 않음", userId, today, courseType);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewRecordsInternal(Integer userId, String courseType, List<DishRecord> recordsToSave) {
        if (recordsToSave == null || recordsToSave.isEmpty()) {
            logger.info("사용자 ID [{}], 코스 타입 [{}]에 대해 저장할 새 식단 기록 없음", userId, courseType);
            return;
        }
        logger.info("사용자 ID [{}], 코스 타입 [{}]에 대해 {}건의 새 식단 기록 저장 시도", userId, courseType, recordsToSave.size());
        List<DishRecord> savedRecords = new ArrayList<>();
        for (DishRecord record : recordsToSave) {
            // userId, courseType, recordedAt은 컨트롤러에서 이미 설정되었거나 여기서 설정 필요
            record.setUserId(userId); // 컨트롤러에서 이미 설정했다면 중복일 수 있으나, 확실히 하기 위해
            record.setCourseType(courseType); // 컨트롤러에서 이미 설정했다면 중복일 수 있으나, 확실히 하기 위해
            record.setRecordedAt(LocalDateTime.now()); // 항상 현재 시간으로 기록
            
            // scheduleId와 dishId는 record 객체에 이미 있어야 함
            if (record.getScheduleId() == null || record.getDishId() == null) {
                logger.error("Schedule ID 또는 Dish ID가 누락되어 레코드를 저장할 수 없습니다: {}", record);
                // 혹은 예외를 던져서 트랜잭션 롤백 유도
                // throw new IllegalArgumentException("Schedule ID 또는 Dish ID가 누락되었습니다.");
                continue; // 이 레코드는 건너뜀
            }

            // 중복 저장 방지 로직은 여기서 필요 없음. 삭제 후 저장이므로.
            // 만약 필요하다면 findByUserIdAndScheduleIdAndDishId 등으로 확인 후 저장
            savedRecords.add(dishRecordRepository.save(record));
        }
        logger.info("사용자 ID [{}], 코스 타입 [{}]에 대해 {}건의 새 식단 기록 저장 완료", userId, courseType, savedRecords.size());
    }

    @Transactional
    public List<DishRecord> createNewMealRecords(Integer userId, String courseType, List<DishRecord> records) {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException("저장할 식단 정보가 없습니다.");
        }
        // 프론트에서 schedule_id를 모든 meal 객체에 넣어준다는 가정
        // 또는 첫번째 meal의 schedule_id를 사용 (모든 meal이 동일한 schedule_id를 가진다고 가정)
        Integer scheduleIdForCheck = records.get(0).getScheduleId();
        if(scheduleIdForCheck == null) {
             throw new IllegalArgumentException("Schedule ID가 누락되었습니다.");
        }

        List<DishRecord> existingRecords = dishRecordRepository.findByUserIdAndScheduleId(userId, scheduleIdForCheck);
        if (!existingRecords.isEmpty()) {
            // 프론트엔드와 협의된 정책에 따라 처리:
            // 1. 에러 반환
            // throw new IllegalStateException("이미 해당 날짜/코스에 저장된 식단 기록이 있습니다. 수정을 이용해주세요.");
            // 2. 혹은 덮어쓰기 (이 경우 updateMealRecords와 동일해짐)
            // 여기서는 신규 생성이므로, 기존 기록이 있으면 오류를 발생시키는 것이 적절할 수 있습니다.
            // 하지만 현재 프론트엔드는 isEditMode로 분기하므로, 이 메서드는 isEditMode=false일 때만 호출될 것으로 예상.
            // 따라서, 별도의 체크 없이 바로 저장 진행 (만약의 경우를 대비해 로깅은 남김)
            logger.warn("사용자 ID [{}], 스케줄 ID [{}]에 이미 기록이 있지만, 신규 저장 요청으로 간주하고 진행합니다.", userId, scheduleIdForCheck);
        }
        
        List<DishRecord> savedRecords = new ArrayList<>();
        for (DishRecord record : records) {
            record.setUserId(userId);
            record.setCourseType(courseType);
            record.setRecordedAt(LocalDateTime.now());
            // scheduleId, dishId는 이미 DTO 변환 과정에서 설정되었다고 가정
            if (record.getScheduleId() == null || record.getDishId() == null) {
                 logger.error("Schedule ID 또는 Dish ID가 누락되어 레코드를 저장할 수 없습니다: {}", record);
                 continue;
            }
            savedRecords.add(dishRecordRepository.save(record));
        }
        logger.info("사용자 ID [{}], 코스 타입 [{}] 신규 식단 기록 {}건 저장 완료", userId, courseType, savedRecords.size());
        return savedRecords;
    }

    @Transactional
    public void replaceMealRecords(Integer userId, String courseType, List<DishRecord> newRecords) {
        if (newRecords == null) {
            logger.warn("사용자 ID [{}], 코스 타입 [{}]의 수정 요청으로 newRecords가 null입니다. 모든 기록 삭제로 간주됩니다.", userId, courseType);
            newRecords = new ArrayList<>();
        }

        try {
            LocalDate today = LocalDate.now();
            
            // 1. 오늘 날짜의 모든 코스(A, B)에 대한 사용자의 기존 기록을 삭제
            List<CourseSchedule> todaySchedules = courseScheduleRepository.findByScheduleDate(today);
            for (CourseSchedule schedule : todaySchedules) {
                try {
                    deleteRecordsByUserIdAndScheduleId(userId, schedule.getScheduleId());
                    logger.info("사용자 ID [{}]의 스케줄 ID [{}] ({} 코스) 기록 삭제 완료", 
                        userId, schedule.getScheduleId(), schedule.getCourseType());
                } catch (Exception e) {
                    logger.warn("기존 기록 삭제 중 오류 발생 (스케줄 ID: {}): {}", schedule.getScheduleId(), e.getMessage());
                }
            }
            
            // 트랜잭션이 완전히 커밋되도록 잠시 대기
            Thread.sleep(100);

            // 2. 새로운 기록 저장 준비
            if (!newRecords.isEmpty()) {
                // 현재 선택된 코스의 schedule_id 조회
                CourseSchedule currentSchedule = courseScheduleRepository.findByScheduleDateAndCourseType(today, courseType)
                    .orElseThrow(() -> new RuntimeException("오늘의 " + courseType + " 코스 스케줄을 찾을 수 없습니다."));
                Integer scheduleIdToSave = currentSchedule.getScheduleId();

                // 중복 체크를 위한 Set
                Set<String> uniqueRecords = new HashSet<>();
                
                for (DishRecord record : newRecords) {
                    // 중복 체크를 위한 키 생성
                    String recordKey = userId + "-" + scheduleIdToSave + "-" + record.getDishId();
                    
                    // 중복이 아닌 경우에만 저장
                    if (uniqueRecords.add(recordKey)) {
                        record.setUserId(userId);
                        record.setScheduleId(scheduleIdToSave); // 현재 선택된 코스의 schedule_id로 설정
                        record.setCourseType(courseType);
                        record.setRecordedAt(LocalDateTime.now());
                        
                        // 이미 존재하는지 한번 더 확인
                        List<DishRecord> existingRecords = dishRecordRepository
                            .findByUserIdAndScheduleIdAndDishId(userId, scheduleIdToSave, record.getDishId());
                        
                        if (existingRecords.isEmpty()) {
                            try {
                                dishRecordRepository.save(record);
                                logger.info("새 기록 저장 성공: userId={}, scheduleId={}, dishId={}, courseType={}", 
                                    userId, scheduleIdToSave, record.getDishId(), courseType);
                            } catch (Exception e) {
                                logger.warn("개별 기록 저장 실패: {}", e.getMessage());
                            }
                        } else {
                            logger.warn("이미 존재하는 기록 건너뜀: userId={}, scheduleId={}, dishId={}", 
                                userId, scheduleIdToSave, record.getDishId());
                        }
                    } else {
                        logger.warn("중복된 기록 건너뜀: {}", recordKey);
                    }
                }
                logger.info("사용자 ID [{}], 코스 타입 [{}]에 대해 새 기록 저장 완료", userId, courseType);
            } else {
                logger.info("사용자 ID [{}], 코스 타입 [{}]에 대해 선택된 음식이 없어 새 기록을 저장하지 않음", userId, courseType);
            }
        } catch (Exception e) {
            logger.error("식단 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("식단 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 5일간의 기록을 가져오는 메소드 추가
    public List<Map<String, Object>> getLast5DaysRecords(Integer userId) {
        LocalDateTime endOfToday = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        LocalDateTime startOf5DaysAgo = endOfToday.minusDays(4).withHour(0).withMinute(0).withSecond(0);
        
        List<DishRecord> records = dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, startOf5DaysAgo, endOfToday);
        
        // 날짜별로 그룹화하여 결과 생성
        Map<String, Map<String, Double>> dailyNutrients = new HashMap<>();
        
        // 일자별, 영양소별 합계 데이터 구성
        for (DishRecord record : records) {
            // 날짜만 추출 (yyyy-MM-dd 형식)
            String dateKey = record.getRecordedAt().toLocalDate().toString();
            
            // 해당 날짜의 영양소 맵을 가져오거나 새로 생성
            Map<String, Double> dayNutrients = dailyNutrients.getOrDefault(dateKey, new HashMap<>());
            
            // 음식 정보 조회
            Optional<Dish> dishOpt = dishRepository.findById(record.getDishId());
            
            if (dishOpt.isPresent()) {
                Dish dish = dishOpt.get();
                
                // 영양소 값 누적
                dayNutrients.put("calories", dayNutrients.getOrDefault("calories", 0.0) + (dish.getCalorieKcal() != null ? dish.getCalorieKcal() : 0.0));
                dayNutrients.put("protein", dayNutrients.getOrDefault("protein", 0.0) + (dish.getProteinG() != null ? dish.getProteinG() : 0.0));
                dayNutrients.put("carbohydrate", dayNutrients.getOrDefault("carbohydrate", 0.0) + (dish.getCarbohydrateG() != null ? dish.getCarbohydrateG() : 0.0));
                dayNutrients.put("fat", dayNutrients.getOrDefault("fat", 0.0) + (dish.getFatG() != null ? dish.getFatG() : 0.0));
                dayNutrients.put("sugar", dayNutrients.getOrDefault("sugar", 0.0) + (dish.getSugarG() != null ? dish.getSugarG() : 0.0));
            }
            
            dailyNutrients.put(dateKey, dayNutrients);
        }
        
        // 최근 5일 데이터 준비 (오늘부터 5일 전까지)
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // 최근 5일치 날짜를 먼저 생성
        for (int i = 4; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateKey = date.toString();
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dateKey);
            
            // 해당 날짜의 데이터가 있으면 사용, 없으면 빈 값 (0) 설정
            Map<String, Double> nutrients = dailyNutrients.getOrDefault(dateKey, new HashMap<>());
            
            // 모든 필요한 영양소 키가 있는지 확인하고, 없으면 0으로 설정
            if (!nutrients.containsKey("calories")) nutrients.put("calories", 0.0);
            if (!nutrients.containsKey("protein")) nutrients.put("protein", 0.0);
            if (!nutrients.containsKey("carbohydrate")) nutrients.put("carbohydrate", 0.0);
            if (!nutrients.containsKey("fat")) nutrients.put("fat", 0.0);
            if (!nutrients.containsKey("sugar")) nutrients.put("sugar", 0.0);
            
            dayData.put("nutrients", nutrients);
            result.add(dayData);
        }
        
        // 날짜순 정렬
        result.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
        
        return result;
    }

    /**
     * 오늘 사용자가 섭취한 영양소 정보를 조회합니다.
     */
    public Map<String, Object> getTodayNutrients(Integer userId) {
        // 오늘의 시작과 끝 시간 계산
        LocalDateTime startOfToday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfToday = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        // 오늘의 식단 기록 조회
        List<DishRecord> records = dishRecordRepository.findByUserIdAndRecordedAtBetween(userId, startOfToday, endOfToday);
        
        // 영양소 합계를 위한 변수 초기화
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarbohydrate = 0.0;
        double totalFat = 0.0;
        double totalSugar = 0.0;
        
        // 음식별 영양소 합산
        for (DishRecord record : records) {
            // 음식 정보 조회
            Optional<Dish> dishOpt = dishRepository.findById(record.getDishId());
            
            if (dishOpt.isPresent()) {
                Dish dish = dishOpt.get();
                
                // 영양소 합산 (null 체크 포함)
                totalCalories += (dish.getCalorieKcal() != null ? dish.getCalorieKcal() : 0.0);
                totalProtein += (dish.getProteinG() != null ? dish.getProteinG() : 0.0);
                totalCarbohydrate += (dish.getCarbohydrateG() != null ? dish.getCarbohydrateG() : 0.0);
                totalFat += (dish.getFatG() != null ? dish.getFatG() : 0.0);
                totalSugar += (dish.getSugarG() != null ? dish.getSugarG() : 0.0);
            }
        }
        
        // 결과 맵 생성
        Map<String, Object> nutrientsMap = new HashMap<>();
        nutrientsMap.put("calories", totalCalories);
        nutrientsMap.put("protein", totalProtein);
        nutrientsMap.put("carbohydrate", totalCarbohydrate);
        nutrientsMap.put("fat", totalFat);
        nutrientsMap.put("sugar", totalSugar);
        nutrientsMap.put("mealCount", records.size());
        
        logger.info("사용자 ID [{}]의 오늘 영양소 정보 조회 완료: 칼로리 {}kcal, 단백질 {}g, 탄수화물 {}g, 지방 {}g, 당 {}g", 
            userId, totalCalories, totalProtein, totalCarbohydrate, totalFat, totalSugar);
        
        return nutrientsMap;
    }
}