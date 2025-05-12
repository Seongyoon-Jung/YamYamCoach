package com.yamyam.diet.controller;

import com.yamyam.diet.entity.CourseSchedule;
import com.yamyam.diet.entity.Dish;
import com.yamyam.diet.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayCourses() {
        LocalDate today = LocalDate.now();
        Map<String, Object> courses = courseService.getCoursesByDate(today);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{date}")
    public ResponseEntity<Map<String, Object>> getCoursesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> courses = courseService.getCoursesByDate(date);
        return ResponseEntity.ok(courses);
    }
} 