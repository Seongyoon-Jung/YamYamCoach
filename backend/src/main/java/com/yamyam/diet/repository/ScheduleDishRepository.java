package com.yamyam.diet.repository;

import com.yamyam.diet.entity.ScheduleDish;
import com.yamyam.diet.entity.ScheduleDishId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleDishRepository extends JpaRepository<ScheduleDish, ScheduleDishId> {
    List<ScheduleDish> findByIdScheduleId(Integer scheduleId);
    
    boolean existsByDish_DishIdAndCourseSchedule_ScheduleId(Integer DishId, Integer ScheduleId);
} 