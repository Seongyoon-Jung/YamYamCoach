const express = require('express');
const router = express.Router();
const recipeController = require('../controllers/RecipeController');
const { isAuth } = require('../middlewares/auth');

// 모든 레시피 조회 (필터링 지원)
router.get('/recipes', recipeController.getAllRecipes);

// 특정 레시피 상세 조회
router.get('/recipes/:id', recipeController.getRecipeById);

// 연관 레시피 조회
router.get('/recipes/related/:id', recipeController.getRelatedRecipes);

// 좋아요 상태 조회
router.get('/recipes/:id/like-status', isAuth, recipeController.getLikeStatus);

// 좋아요 추가
router.post('/recipes/:id/like', isAuth, recipeController.addLike);

// 좋아요 취소
router.delete('/recipes/:id/like', isAuth, recipeController.removeLike);

// 추가적인 API들은 여기에 정의...

module.exports = router; 