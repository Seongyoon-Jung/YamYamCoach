package com.yamyam.recipe.dto;

import java.time.LocalDateTime;

import com.yamyam.recipe.model.Recipe;

public class RecipeResponse {
    private Long id;
    private String name;
    private String category;
    private String ingredients;
    private String description;
    private String content;
    private Integer cookTimeMinutes;
    private String difficulty;
    private Integer servings;
    private String imageUrl;
    private Long userId;
    private String userName;
    private String userProfileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public RecipeResponse() {
    }

    // Recipe 엔티티를 기반으로 한 생성자
    public RecipeResponse(Recipe recipe) {
        this.id = recipe.getId();
        this.name = recipe.getName();
        this.category = recipe.getCategory();
        this.ingredients = recipe.getIngredients();
        this.description = recipe.getDescription();
        this.content = recipe.getContent();
        this.cookTimeMinutes = recipe.getCookTimeMinutes();
        this.difficulty = recipe.getDifficulty();
        this.servings = recipe.getServings();
        this.imageUrl = recipe.getImageUrl();
        
        if (recipe.getUser() != null) {
            this.userId = Long.valueOf(recipe.getUser().getUserId());
            this.userName = recipe.getUser().getUsername();
            this.userProfileUrl = recipe.getUser().getProfileUrl();
        }
        
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
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
} 