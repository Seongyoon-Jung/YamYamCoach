package com.yamyam.recipe.dto;

public class RecipeRequest {
    private String name;
    private String category;
    private String ingredients;
    private String description;
    private String content;
    private Integer cookTimeMinutes;
    private String difficulty;
    private Integer servings;
    private String imageUrl;
    
    // 기본 생성자
    public RecipeRequest() {
    }
    
    // 모든 필드를 포함한 생성자
    public RecipeRequest(String name, String category, String ingredients, String description, String content, 
                        Integer cookTimeMinutes, String difficulty, Integer servings, String imageUrl) {
        this.name = name;
        this.category = category;
        this.ingredients = ingredients;
        this.description = description;
        this.content = content;
        this.cookTimeMinutes = cookTimeMinutes;
        this.difficulty = difficulty;
        this.servings = servings;
        this.imageUrl = imageUrl;
    }
    
    // 게터와 세터
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }
    
    public void setCookTimeMinutes(Integer cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public Integer getServings() {
        return servings;
    }
    
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    
    // imageUrl getter/setter
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @Override
    public String toString() {
        return "RecipeRequest{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", cookTimeMinutes=" + cookTimeMinutes +
                ", difficulty='" + difficulty + '\'' +
                ", servings=" + servings +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
} 