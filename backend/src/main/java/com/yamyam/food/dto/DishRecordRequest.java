package com.yamyam.food.dto;

public class DishRecordRequest {
    private Integer schedule_id;
    private Integer dish_id;

    public DishRecordRequest() {
    }

    public DishRecordRequest(Integer schedule_id, Integer dish_id) {
        this.schedule_id = schedule_id;
        this.dish_id = dish_id;
    }

    public Integer getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    public Integer getDish_id() {
        return dish_id;
    }

    public void setDish_id(Integer dish_id) {
        this.dish_id = dish_id;
    }

    @Override
    public String toString() {
        return "DishRecordRequest [schedule_id=" + schedule_id + ", dish_id=" + dish_id + "]";
    }
}