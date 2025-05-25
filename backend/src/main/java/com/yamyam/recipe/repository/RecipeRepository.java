package com.yamyam.recipe.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yamyam.entity.UserEntity;
import com.yamyam.recipe.model.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // 카테고리별 레시피 검색
    List<Recipe> findByCategory(String category);
    
    // 사용자별 레시피 검색
    List<Recipe> findByUser(UserEntity user);
    
    // 사용자 ID별 레시피 검색
    @Query("SELECT r FROM Recipe r WHERE r.user.userId = :userId")
    List<Recipe> findByUserId(@Param("userId") Long userId);
    
    // 이름으로 레시피 검색 (포함 검색)
    List<Recipe> findByNameContaining(String name);
    
    // 이름으로 레시피 검색 (페이징)
    Page<Recipe> findByNameContaining(String name, Pageable pageable);
    
    // 카테고리별 레시피 검색 (페이징)
    Page<Recipe> findByCategory(String category, Pageable pageable);
    
    // 다중 검색 조건으로 레시피 검색
    @Query("SELECT r FROM Recipe r WHERE " +
           "(:name IS NULL OR r.name LIKE %:name%) AND " +
           "(:category IS NULL OR r.category = :category)")
    Page<Recipe> searchRecipes(
            @Param("name") String name,
            @Param("category") String category,
            Pageable pageable);
    
    // 검색어로 레시피 검색
    @Query("SELECT r FROM Recipe r WHERE r.name LIKE %:keyword% OR r.content LIKE %:keyword% OR r.ingredients LIKE %:keyword%")
    List<Recipe> searchByKeyword(@Param("keyword") String keyword);
    
    // 모든 레시피 조회 (최신순)
    List<Recipe> findAllByOrderByCreatedAtDesc();
} 