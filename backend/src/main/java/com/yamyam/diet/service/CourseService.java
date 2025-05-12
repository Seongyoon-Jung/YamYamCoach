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
        List<CourseSchedule> schedules = courseScheduleRepository.findByScheduleDate(date);
        Map<String, Object> result = new HashMap<>();
        result.put("date", date.toString());
        
        List<Map<String, Object>> courses = new ArrayList<>();
        
        for (CourseSchedule schedule : schedules) {
            Map<String, Object> course = new HashMap<>();
            course.put("type", schedule.getCourseType());
            course.put("course_name", schedule.getCourseName());
            
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
        }
        
        result.put("courses", courses);
        return result;
    }
} 