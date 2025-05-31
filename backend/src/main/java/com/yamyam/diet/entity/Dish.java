package com.yamyam.diet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dish_id")
    private Integer dishId;

    @Column(name = "dish_name", nullable = false, length = 100)
    private String dishName;

    @Column(name = "calorie_kcal")
    private Double calorieKcal;

    @Column(name = "carbohydrate_g")
    private Double carbohydrateG;

    @Column(name = "sugar_g")
    private Double sugarG;

    @Column(name = "fiber_g")
    private Double fiberG;

    @Column(name = "protein_g")
    private Double proteinG;

    @Column(name = "fat_g")
    private Double fatG;

    @Column(name = "saturated_fat_g")
    private Double saturatedFatG;

    @Column(name = "trans_fat_g")
    private Double transFatG;

    @Column(name = "ash_g")
    private Double ashG;

    @Column(name = "sodium_mg")
    private Double sodiumMg;

    @Column(name = "calcium_mg")
    private Double calciumMg;

    @Column(name = "iron_mg")
    private Double ironMg;

    @Column(name = "phosphorus_mg")
    private Double phosphorusMg;

    @Column(name = "potassium_mg")
    private Double potassiumMg;

    @Column(name = "vitamin_a_mcg")
    private Double vitaminAMcg;

    @Column(name = "vitamin_b1_mg")
    private Double vitaminB1Mg;

    @Column(name = "vitamin_b2_mg")
    private Double vitaminB2Mg;

    @Column(name = "vitamin_b3_mg")
    private Double vitaminB3Mg;

    @Column(name = "folic_acid_mcg")
    private Double folicAcidMcg;

    @Column(name = "vitamin_c_mg")
    private Double vitaminCMg;

    @Column(name = "vitamin_d_mcg")
    private Double vitaminDMcg;

    @Column(name = "vitamin_e_mg")
    private Double vitaminEMg;

    // Getters
    public int getDishId() {
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
    public void setDishId(int dishId) {
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