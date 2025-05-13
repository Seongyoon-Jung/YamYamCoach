package com.yamyam.diet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dish_record")
public class DishRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer scheduleId;

    @Column(nullable = false)
    private Integer dishId;

    @Column(length = 1)
    private String courseType; // A/B

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    public DishRecord() {}

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public Integer getDishId() { return dishId; }
    public void setDishId(Integer dishId) { this.dishId = dishId; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    @Override
    public String toString() {
        return "DishRecord{" +
                "recordId=" + recordId +
                ", userId=" + userId +
                ", scheduleId=" + scheduleId +
                ", dishId=" + dishId +
                ", courseType='" + courseType + '\'' +
                ", recordedAt=" + recordedAt +
                '}';
    }
}