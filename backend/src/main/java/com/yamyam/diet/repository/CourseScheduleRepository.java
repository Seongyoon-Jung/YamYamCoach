package com.yamyam.diet.repository;

import com.yamyam.diet.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Integer> {
    List<CourseSchedule> findByScheduleDate(LocalDate date);
    
    // 특정 날짜와 코스 타입으로 스케줄 찾기
    Optional<CourseSchedule> findByScheduleDateAndCourseType(LocalDate date, String courseType);
    
    boolean existsByScheduleDateAndCourseType(LocalDate scheduleDate, String courseType);
} 