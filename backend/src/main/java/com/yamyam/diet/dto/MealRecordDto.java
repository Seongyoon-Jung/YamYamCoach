package com.yamyam.diet.dto;

import com.yamyam.diet.entity.MealRecord;
import java.time.LocalDate;
import java.util.Objects;

public class MealRecordDto {
    private Long id;
    private Long userId;
    private String recordedAt;
    private String courseType;
    private String dishName;
    private Double calories;
    private Double carbs;
    private Double protein;
    private Double fat;
    private Double sugar;
    
    // 기본 생성자
    public MealRecordDto() {
    }
    
    // 모든 필드 생성자
    public MealRecordDto(Long id, Long userId, String recordedAt, String courseType, String dishName, 
                       Double calories, Double carbs, Double protein, Double fat, Double sugar) {
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
    }
    
    // 빌더 패턴 구현
    public static MealRecordDtoBuilder builder() {
        return new MealRecordDtoBuilder();
    }
    
    public static class MealRecordDtoBuilder {
        private Long id;
        private Long userId;
        private String recordedAt;
        private String courseType;
        private String dishName;
        private Double calories;
        private Double carbs;
        private Double protein;
        private Double fat;
        private Double sugar;
        
        MealRecordDtoBuilder() {
        }
        
        public MealRecordDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public MealRecordDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public MealRecordDtoBuilder recordedAt(String recordedAt) {
            this.recordedAt = recordedAt;
            return this;
        }
        
        public MealRecordDtoBuilder courseType(String courseType) {
            this.courseType = courseType;
            return this;
        }
        
        public MealRecordDtoBuilder dishName(String dishName) {
            this.dishName = dishName;
            return this;
        }
        
        public MealRecordDtoBuilder calories(Double calories) {
            this.calories = calories;
            return this;
        }
        
        public MealRecordDtoBuilder carbs(Double carbs) {
            this.carbs = carbs;
            return this;
        }
        
        public MealRecordDtoBuilder protein(Double protein) {
            this.protein = protein;
            return this;
        }
        
        public MealRecordDtoBuilder fat(Double fat) {
            this.fat = fat;
            return this;
        }
        
        public MealRecordDtoBuilder sugar(Double sugar) {
            this.sugar = sugar;
            return this;
        }
        
        public MealRecordDto build() {
            return new MealRecordDto(id, userId, recordedAt, courseType, dishName, 
                                   calories, carbs, protein, fat, sugar);
        }
    }
    
    // Entity -> DTO 변환
    public static MealRecordDto fromEntity(MealRecord entity) {
        return MealRecordDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .recordedAt(entity.getRecordedAt().toString())
                .courseType(entity.getCourseType().name())
                .dishName(entity.getDishName())
                .calories(entity.getCalories())
                .carbs(entity.getCarbs())
                .protein(entity.getProtein())
                .fat(entity.getFat())
                .sugar(entity.getSugar())
                .build();
    }
    
    // DTO -> Entity 변환
    public MealRecord toEntity() {
        return MealRecord.builder()
                .id(this.id)
                .userId(this.userId)
                .recordedAt(LocalDate.parse(this.recordedAt))
                .courseType(MealRecord.CourseType.valueOf(this.courseType))
                .dishName(this.dishName)
                .calories(this.calories)
                .carbs(this.carbs)
                .protein(this.protein)
                .fat(this.fat)
                .sugar(this.sugar)
                .build();
    }
    
    // Getter/Setter 메서드
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
    
    public String getRecordedAt() {
        return recordedAt;
    }
    
    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }
    
    public String getCourseType() {
        return courseType;
    }
    
    public void setCourseType(String courseType) {
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealRecordDto that = (MealRecordDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(recordedAt, that.recordedAt) &&
                Objects.equals(courseType, that.courseType) &&
                Objects.equals(dishName, that.dishName) &&
                Objects.equals(calories, that.calories) &&
                Objects.equals(carbs, that.carbs) &&
                Objects.equals(protein, that.protein) &&
                Objects.equals(fat, that.fat) &&
                Objects.equals(sugar, that.sugar);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, recordedAt, courseType, dishName, 
                           calories, carbs, protein, fat, sugar);
    }
    
    @Override
    public String toString() {
        return "MealRecordDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", recordedAt='" + recordedAt + '\'' +
                ", courseType='" + courseType + '\'' +
                ", dishName='" + dishName + '\'' +
                ", calories=" + calories +
                ", carbs=" + carbs +
                ", protein=" + protein +
                ", fat=" + fat +
                ", sugar=" + sugar +
                '}';
    }
} 