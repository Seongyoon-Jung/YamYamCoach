package com.yamyam.diet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "schedule_dish")
public class ScheduleDish {

    @EmbeddedId
    private ScheduleDishId id;

    @ManyToOne
    @MapsId("scheduleId")
    @JoinColumn(name = "schedule_id", nullable = false)
    private CourseSchedule courseSchedule;

    @ManyToOne
    @MapsId("dishId")
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(name = "serving_order", nullable = false)
    private Integer servingOrder;

    @Column(length = 255)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at",
            nullable = false,
            updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",
            nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // --- JPA Lifecycle Callbacks ---
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters ---
    public ScheduleDishId getId() {
        return id;
    }

    public CourseSchedule getCourseSchedule() {
        return courseSchedule;
    }

    public Dish getDish() {
        return dish;
    }

    public Integer getServingOrder() {
        return servingOrder;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // --- Setters ---
    public void setId(ScheduleDishId id) {
        this.id = id;
    }

    public void setCourseSchedule(CourseSchedule courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public void setServingOrder(Integer servingOrder) {
        this.servingOrder = servingOrder;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // timestamps는 외부에서 변경하지 않도록 protected로 제한
    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    protected void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
