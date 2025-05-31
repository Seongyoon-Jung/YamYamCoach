package com.yamyam.recipe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"recipe_id", "user_id"})
})
public class RecipeLike {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // 기본 생성자
    public RecipeLike() {
    }
    
    // 생성자
    public RecipeLike(Long recipeId, Long userId) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
    
    // 생성 전 실행
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // 게터와 세터
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRecipeId() {
        return recipeId;
    }
    
    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 