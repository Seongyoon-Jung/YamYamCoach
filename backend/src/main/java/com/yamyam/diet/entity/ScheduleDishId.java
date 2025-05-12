package com.yamyam.diet.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ScheduleDishId implements Serializable {
    private Long scheduleId;
    private Long dishId;

    public ScheduleDishId() {
    }

    public ScheduleDishId(Long scheduleId, Long dishId) {
        this.scheduleId = scheduleId;
        this.dishId = dishId;
    }

    // Getters
    public Long getScheduleId() {
        return scheduleId;
    }

    public Long getDishId() {
        return dishId;
    }

    // Setters
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleDishId that = (ScheduleDishId) o;

        if (!scheduleId.equals(that.scheduleId)) return false;
        return dishId.equals(that.dishId);
    }

    @Override
    public int hashCode() {
        int result = scheduleId.hashCode();
        result = 31 * result + dishId.hashCode();
        return result;
    }
} 