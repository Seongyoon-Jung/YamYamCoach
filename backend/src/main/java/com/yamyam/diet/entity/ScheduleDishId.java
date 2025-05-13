package com.yamyam.diet.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ScheduleDishId implements Serializable {
    private Integer scheduleId;
    private Integer dishId;

    public ScheduleDishId() {
    }

    public ScheduleDishId(Integer scheduleId, Integer dishId) {
        this.scheduleId = scheduleId;
        this.dishId = dishId;
    }

    // Getters
    public Integer getScheduleId() {
        return scheduleId;
    }

    public Integer getDishId() {
        return dishId;
    }

    // Setters
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setDishId(Integer dishId) {
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