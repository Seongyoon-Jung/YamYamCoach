package com.yamyam.diet.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "extra_dish_record")
public class MealRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recorded_at", nullable = false)
    private LocalDate recordedAt;

    @Column(name = "course_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    @Column(name = "dish_name", nullable = false)
    private String dishName;

    @Column(name = "calories")
    private Double calories;

    @Column(name = "carbs")
    private Double carbs;

    @Column(name = "protein")
    private Double protein;

    @Column(name = "fat")
    private Double fat;

    @Column(name = "sugar")
    private Double sugar;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MealRecord() {
    }

    public MealRecord(Long id, Long userId, LocalDate recordedAt, CourseType courseType, String dishName, 
                      Double calories, Double carbs, Double protein, Double fat, Double sugar, 
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.recordedAt = recordedAt;
        this.courseType = courseType;
        this.dishName = dishName;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.sugar = sugar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 빌더 패턴 구현
    public static MealRecordBuilder builder() {
        return new MealRecordBuilder();
    }

    public static class MealRecordBuilder {
        private Long id;
        private Long userId;
        private LocalDate recordedAt;
        private CourseType courseType;
        private String dishName;
        private Double calories;
        private Double carbs;
        private Double protein;
        private Double fat;
        private Double sugar;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        MealRecordBuilder() {
        }

        public MealRecordBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MealRecordBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public MealRecordBuilder recordedAt(LocalDate recordedAt) {
            this.recordedAt = recordedAt;
            return this;
        }

        public MealRecordBuilder courseType(CourseType courseType) {
            this.courseType = courseType;
            return this;
        }

        public MealRecordBuilder dishName(String dishName) {
            this.dishName = dishName;
            return this;
        }

        public MealRecordBuilder calories(Double calories) {
            this.calories = calories;
            return this;
        }

        public MealRecordBuilder carbs(Double carbs) {
            this.carbs = carbs;
            return this;
        }

        public MealRecordBuilder protein(Double protein) {
            this.protein = protein;
            return this;
        }

        public MealRecordBuilder fat(Double fat) {
            this.fat = fat;
            return this;
        }

        public MealRecordBuilder sugar(Double sugar) {
            this.sugar = sugar;
            return this;
        }

        public MealRecordBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MealRecordBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public MealRecord build() {
            return new MealRecord(id, userId, recordedAt, courseType, dishName, 
                                 calories, carbs, protein, fat, sugar, createdAt, updatedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDate recordedAt) {
        this.recordedAt = recordedAt;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getSugar() {
        return sugar;
    }

    public void setSugar(Double sugar) {
        this.sugar = sugar;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealRecord that = (MealRecord) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(recordedAt, that.recordedAt) &&
                courseType == that.courseType &&
                Objects.equals(dishName, that.dishName) &&
                Objects.equals(calories, that.calories) &&
                Objects.equals(carbs, that.carbs) &&
                Objects.equals(protein, that.protein) &&
                Objects.equals(fat, that.fat) &&
                Objects.equals(sugar, that.sugar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, recordedAt, courseType, dishName, calories, 
                           carbs, protein, fat, sugar);
    }

    @Override
    public String toString() {
        return "MealRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", recordedAt=" + recordedAt +
                ", courseType=" + courseType +
                ", dishName='" + dishName + '\'' +
                ", calories=" + calories +
                ", carbs=" + carbs +
                ", protein=" + protein +
                ", fat=" + fat +
                ", sugar=" + sugar +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public enum CourseType {
        BREAKFAST, LUNCH, DINNER, SNACK
    }
} 