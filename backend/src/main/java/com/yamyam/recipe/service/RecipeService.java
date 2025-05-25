package com.yamyam.recipe.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.yamyam.recipe.dto.RecipeRequest;
import com.yamyam.recipe.dto.RecipeResponse;

public interface RecipeService {
    
    // 레시피 생성
    RecipeResponse createRecipe(RecipeRequest recipeRequest, Long userId);
    
    // 이미지 포함 레시피 생성
    RecipeResponse createRecipeWithImage(RecipeRequest recipeRequest, MultipartFile imageFile, Long userId);
    
    // 레시피 조회
    RecipeResponse getRecipe(Long id);
    
    // 모든 레시피 조회
    List<RecipeResponse> getAllRecipes();
    
    // 모든 레시피 페이징 조회
    Page<RecipeResponse> getAllRecipes(Pageable pageable);
    
    // 레시피 수정
    RecipeResponse updateRecipe(Long id, RecipeRequest recipeRequest, Long userId);
    
    // 이미지 포함 레시피 수정
    RecipeResponse updateRecipeWithImage(Long id, RecipeRequest recipeRequest, MultipartFile imageFile, Long userId);
    
    // 레시피 삭제
    void deleteRecipe(Long id, Long userId);
    
    // 사용자별 레시피 조회
    List<RecipeResponse> getRecipesByUserId(Long userId);
    
    // 카테고리별 레시피 조회
    List<RecipeResponse> getRecipesByCategory(String category);
    
    // 이름으로 레시피 검색
    List<RecipeResponse> searchRecipesByName(String name);
    
    // 다중 조건으로 레시피 검색 (페이징)
    Page<RecipeResponse> searchRecipes(String name, String category, Pageable pageable);
    
    // 레시피 좋아요 수 증가
    void incrementLikes(Long recipeId);
} 