package com.yamyam.recipe.repository;

import com.yamyam.recipe.model.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {
    
    Optional<RecipeLike> findByRecipeIdAndUserId(Long recipeId, Long userId);
    
    int countByRecipeId(Long recipeId);
    
    void deleteByRecipeIdAndUserId(Long recipeId, Long userId);
} 