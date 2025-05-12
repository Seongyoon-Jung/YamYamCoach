package com.yamyam.diet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dishId;

    @Column(nullable = false, length = 100)
    private String dishName;

    private Double calorieKcal;
    private Double carbohydrateG;
    private Double sugarG;
    private Double fiberG;
    private Double proteinG;
    private Double fatG;
    private Double saturatedFatG;
    private Double transFatG;
    private Double ashG;
    private Double sodiumMg;
    private Double calciumMg;
    private Double ironMg;
    private Double phosphorusMg;
    private Double potassiumMg;
    private Double vitaminAMcg;
    private Double vitaminB1Mg;
    private Double vitaminB2Mg;
    private Double vitaminB3Mg;
    private Double folicAcidMcg;
    private Double vitaminCMg;
    private Double vitaminDMcg;
    private Double vitaminEMg;

    // Getters
    public Long getDishId() {
        return dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public Double getCalorieKcal() {
        return calorieKcal;
    }

    public Double getCarbohydrateG() {
        return carbohydrateG;
    }

    public Double getSugarG() {
        return sugarG;
    }

    public Double getFiberG() {
        return fiberG;
    }

    public Double getProteinG() {
        return proteinG;
    }

    public Double getFatG() {
        return fatG;
    }

    public Double getSaturatedFatG() {
        return saturatedFatG;
    }

    public Double getTransFatG() {
        return transFatG;
    }

    public Double getAshG() {
        return ashG;
    }

    public Double getSodiumMg() {
        return sodiumMg;
    }

    public Double getCalciumMg() {
        return calciumMg;
    }

    public Double getIronMg() {
        return ironMg;
    }

    public Double getPhosphorusMg() {
        return phosphorusMg;
    }

    public Double getPotassiumMg() {
        return potassiumMg;
    }

    public Double getVitaminAMcg() {
        return vitaminAMcg;
    }

    public Double getVitaminB1Mg() {
        return vitaminB1Mg;
    }

    public Double getVitaminB2Mg() {
        return vitaminB2Mg;
    }

    public Double getVitaminB3Mg() {
        return vitaminB3Mg;
    }

    public Double getFolicAcidMcg() {
        return folicAcidMcg;
    }

    public Double getVitaminCMg() {
        return vitaminCMg;
    }

    public Double getVitaminDMcg() {
        return vitaminDMcg;
    }

    public Double getVitaminEMg() {
        return vitaminEMg;
    }

    // Setters
    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setCalorieKcal(Double calorieKcal) {
        this.calorieKcal = calorieKcal;
    }

    public void setCarbohydrateG(Double carbohydrateG) {
        this.carbohydrateG = carbohydrateG;
    }

    public void setSugarG(Double sugarG) {
        this.sugarG = sugarG;
    }

    public void setFiberG(Double fiberG) {
        this.fiberG = fiberG;
    }

    public void setProteinG(Double proteinG) {
        this.proteinG = proteinG;
    }

    public void setFatG(Double fatG) {
        this.fatG = fatG;
    }

    public void setSaturatedFatG(Double saturatedFatG) {
        this.saturatedFatG = saturatedFatG;
    }

    public void setTransFatG(Double transFatG) {
        this.transFatG = transFatG;
    }

    public void setAshG(Double ashG) {
        this.ashG = ashG;
    }

    public void setSodiumMg(Double sodiumMg) {
        this.sodiumMg = sodiumMg;
    }

    public void setCalciumMg(Double calciumMg) {
        this.calciumMg = calciumMg;
    }

    public void setIronMg(Double ironMg) {
        this.ironMg = ironMg;
    }

    public void setPhosphorusMg(Double phosphorusMg) {
        this.phosphorusMg = phosphorusMg;
    }

    public void setPotassiumMg(Double potassiumMg) {
        this.potassiumMg = potassiumMg;
    }

    public void setVitaminAMcg(Double vitaminAMcg) {
        this.vitaminAMcg = vitaminAMcg;
    }

    public void setVitaminB1Mg(Double vitaminB1Mg) {
        this.vitaminB1Mg = vitaminB1Mg;
    }

    public void setVitaminB2Mg(Double vitaminB2Mg) {
        this.vitaminB2Mg = vitaminB2Mg;
    }

    public void setVitaminB3Mg(Double vitaminB3Mg) {
        this.vitaminB3Mg = vitaminB3Mg;
    }

    public void setFolicAcidMcg(Double folicAcidMcg) {
        this.folicAcidMcg = folicAcidMcg;
    }

    public void setVitaminCMg(Double vitaminCMg) {
        this.vitaminCMg = vitaminCMg;
    }

    public void setVitaminDMcg(Double vitaminDMcg) {
        this.vitaminDMcg = vitaminDMcg;
    }

    public void setVitaminEMg(Double vitaminEMg) {
        this.vitaminEMg = vitaminEMg;
    }
} 