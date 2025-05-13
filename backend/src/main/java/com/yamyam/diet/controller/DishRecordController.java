package com.yamyam.diet.controller;

import com.yamyam.diet.entity.DishRecord;
import com.yamyam.diet.service.DishRecordService;
import com.yamyam.dto.SecurityAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/meal-records")
public class DishRecordController {

    @Autowired
    private DishRecordService dishRecordService;

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayRecords(@AuthenticationPrincipal SecurityAccount principal) {
        int userId = principal.getUserId();
        List<DishRecord> records = dishRecordService.getTodayRecords(userId);
        if (records.isEmpty()) return ResponseEntity.ok().body(null);

        Map<String, Object> res = new HashMap<>();
        res.put("course_type", records.get(0).getCourseType());
        List<Map<String, Object>> dishes = new ArrayList<>();
        for (DishRecord r : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("dish_id", r.getDishId());
            map.put("schedule_id", r.getScheduleId());
            dishes.add(map);
        }
        res.put("dishes", dishes);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<Void> saveMealRecords(@AuthenticationPrincipal SecurityAccount principal, @RequestBody MealRecordRequest req) {
        int userId = principal.getUserId();
        List<DishRecord> records = new ArrayList<>();
        for (Map<String, Object> m : req.getMeals()) {
            DishRecord dr = new DishRecord();
            dr.setScheduleId((Integer) m.get("schedule_id"));
            dr.setDishId((Integer) m.get("dish_id"));
            records.add(dr);
        }
        dishRecordService.saveRecords(userId, req.getCourseType(), records);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateMealRecords(@AuthenticationPrincipal SecurityAccount principal, @RequestBody MealRecordRequest req) {
        int userId = principal.getUserId();
        List<DishRecord> records = new ArrayList<>();
        for (Map<String, Object> m : req.getMeals()) {
            DishRecord dr = new DishRecord();
            dr.setScheduleId((Integer) m.get("schedule_id"));
            dr.setDishId((Integer) m.get("dish_id"));
            records.add(dr);
        }
        dishRecordService.updateTodayRecords(userId, req.getCourseType(), records);
        return ResponseEntity.ok().build();
    }

    // 내부 DTO 클래스
    public static class MealRecordRequest {
        private String courseType;
        private List<Map<String, Object>> meals;

        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }
        public List<Map<String, Object>> getMeals() { return meals; }
        public void setMeals(List<Map<String, Object>> meals) { this.meals = meals; }
    }
}