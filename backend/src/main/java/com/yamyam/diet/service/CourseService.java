package com.yamyam.diet.service;

import com.yamyam.diet.entity.CourseSchedule;
import com.yamyam.diet.entity.Dish;
import com.yamyam.diet.repository.CourseScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    public Map<String, Object> getCoursesByDate(LocalDate date) {
        System.out.println("CourseService - 조회 날짜: " + date);
        List<CourseSchedule> schedules = courseScheduleRepository.findByScheduleDate(date);
        System.out.println("CourseService - 조회된 스케줄 수: " + schedules.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("date", date.toString());
        
        List<Map<String, Object>> courses = new ArrayList<>();
        
        for (CourseSchedule schedule : schedules) {
            System.out.println("CourseService - 코스: " + schedule.getCourseType() + 
                              ", 스케줄ID: " + schedule.getScheduleId() + 
                              ", 코스명: " + schedule.getCourseName());
            
            Map<String, Object> course = new HashMap<>();
            course.put("type", schedule.getCourseType());
            course.put("course_name", schedule.getCourseName());
            course.put("schedule_id", schedule.getScheduleId());
            
            List<Map<String, Object>> dishes = new ArrayList<>();
            schedule.getScheduleDishes().forEach(scheduleDish -> {
                Dish dish = scheduleDish.getDish();
                Map<String, Object> dishInfo = new HashMap<>();
                dishInfo.put("id", dish.getDishId());
                dishInfo.put("name", dish.getDishName());
                dishInfo.put("calorie", dish.getCalorieKcal());
                dishInfo.put("protein", dish.getProteinG());
                dishInfo.put("fat", dish.getFatG());
                dishInfo.put("carbohydrate", dish.getCarbohydrateG());
                dishes.add(dishInfo);
            });
            
            course.put("dishes", dishes);
            courses.add(course);
            
            // 생성된 코스 객체 디버깅
            System.out.println("CourseService - 생성된 코스 객체: " + course);
        }
        
        result.put("courses", courses);
        
        // 최종 결과 디버깅
        System.out.println("CourseService - 반환되는 코스 수: " + courses.size());
        return result;
    }
} 