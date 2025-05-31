package com.yamyam.healthLog.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_health_log")
public class DailyHealthLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;
    
    @Column(name = "exercise_minutes", nullable = false)
    private Integer exerciseMinutes;
    
    @Column(name = "water_intake_ml", nullable = false)
    private Integer waterIntakeMl;
    
    @Column(name = "sleep_minutes", nullable = false)
    private Integer sleepMinutes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    public DailyHealthLog() {
        this.exerciseMinutes = 0;
        this.waterIntakeMl = 0;
        this.sleepMinutes = 0;
    }
    
    // 생성자
    public DailyHealthLog(Integer userId, LocalDate logDate) {
        this.userId = userId;
        this.logDate = logDate;
        this.exerciseMinutes = 0;
        this.waterIntakeMl = 0;
        this.sleepMinutes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getLogId() {
        return logId;
    }
    
    public void setLogId(Integer logId) {
        this.logId = logId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public LocalDate getLogDate() {
        return logDate;
    }
    
    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }
    
    public Integer getExerciseMinutes() {
        return exerciseMinutes;
    }
    
    public void setExerciseMinutes(Integer exerciseMinutes) {
        this.exerciseMinutes = exerciseMinutes;
    }
    
    public Integer getWaterIntakeMl() {
        return waterIntakeMl;
    }
    
    public void setWaterIntakeMl(Integer waterIntakeMl) {
        this.waterIntakeMl = waterIntakeMl;
    }
    
    public Integer getSleepMinutes() {
        return sleepMinutes;
    }
    
    public void setSleepMinutes(Integer sleepMinutes) {
        this.sleepMinutes = sleepMinutes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 