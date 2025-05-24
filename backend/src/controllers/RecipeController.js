const db = require('../models/db');
const { validationResult } = require('express-validator');

// 모든 레시피 가져오기 (필터링 지원)
exports.getAllRecipes = async (req, res) => {
  try {
    const { 
      search, 
      category, 
      difficulty, 
      time, 
      sort = 'latest',
      tag
    } = req.query;
    
    let query = `
      SELECT 
        r.id, 
        r.name, 
        r.description, 
        r.category, 
        r.cook_time_minutes, 
        r.difficulty, 
        r.servings,
        r.calories,
        r.protein,
        r.carbs,
        r.fat,
        r.image_url,
        r.created_at,
        r.likes,
        u.username as authorName,
        u.profile_image as authorProfileUrl
      FROM recipes r
      LEFT JOIN users u ON r.author_id = u.id
      WHERE 1=1
    `;
    
    const params = [];
    
    // 검색어 필터링
    if (search) {
      query += ` AND (r.name LIKE ? OR r.description LIKE ?)`;
      params.push(`%${search}%`, `%${search}%`);
    }
    
    // 카테고리 필터링
    if (category) {
      query += ` AND r.category = ?`;
      params.push(category);
    }
    
    // 난이도 필터링
    if (difficulty) {
      query += ` AND r.difficulty = ?`;
      params.push(difficulty);
    }
    
    // 조리 시간 필터링
    if (time) {
      if (time === '15분 이내') {
        query += ` AND r.cook_time_minutes <= 15`;
      } else if (time === '30분 이내') {
        query += ` AND r.cook_time_minutes <= 30`;
      } else if (time === '1시간 이내') {
        query += ` AND r.cook_time_minutes <= 60`;
      } else if (time === '1시간 이상') {
        query += ` AND r.cook_time_minutes > 60`;
      }
    }
    
    // 태그 필터링
    if (tag) {
      query += `
        AND r.id IN (
          SELECT recipe_id FROM recipe_tag_relations rtr
          JOIN recipe_tags rt ON rtr.tag_id = rt.id
          WHERE rt.name = ?
        )
      `;
      params.push(tag);
    }
    
    // 정렬
    if (sort === 'latest') {
      query += ` ORDER BY r.created_at DESC`;
    } else if (sort === 'popular') {
      query += ` ORDER BY r.likes DESC`;
    } else if (sort === 'calorie_asc') {
      query += ` ORDER BY r.calories ASC`;
    } else if (sort === 'calorie_desc') {
      query += ` ORDER BY r.calories DESC`;
    }
    
    const [recipes] = await db.query(query, params);
    
    // 각 레시피의 재료 가져오기
    for (const recipe of recipes) {
      const [ingredients] = await db.query(
        `SELECT name FROM recipe_ingredients WHERE recipe_id = ?`,
        [recipe.id]
      );
      recipe.ingredients = ingredients.map(i => i.name);
      
      // 태그 가져오기
      const [tags] = await db.query(
        `SELECT rt.name FROM recipe_tags rt
         JOIN recipe_tag_relations rtr ON rt.id = rtr.tag_id
         WHERE rtr.recipe_id = ?`,
        [recipe.id]
      );
      recipe.tags = tags.map(t => t.name);
      
      // 영양 정보 구조화
      recipe.nutrition = {
        calories: recipe.calories,
        protein: recipe.protein,
        carbs: recipe.carbs,
        fat: recipe.fat
      };
      
      // 응답에서 중복 필드 제거
      delete recipe.calories;
      delete recipe.protein;
      delete recipe.carbs;
      delete recipe.fat;
    }
    
    res.json(recipes);
  } catch (error) {
    console.error('레시피 조회 중 오류:', error);
    res.status(500).json({ error: '서버 오류가 발생했습니다.' });
  }
};

// 특정 레시피 상세 정보 가져오기
exports.getRecipeById = async (req, res) => {
  try {
    const { id } = req.params;
    
    // 기본 레시피 정보 가져오기
    const [recipes] = await db.query(
      `SELECT 
        r.id, 
        r.name, 
        r.description, 
        r.category, 
        r.cook_time_minutes, 
        r.difficulty, 
        r.servings,
        r.calories,
        r.protein,
        r.carbs,
        r.fat,
        r.image_url,
        r.created_at,
        r.likes,
        u.username as authorName,
        u.profile_image as authorProfileUrl
      FROM recipes r
      LEFT JOIN users u ON r.author_id = u.id
      WHERE r.id = ?`,
      [id]
    );
    
    if (recipes.length === 0) {
      return res.status(404).json({ error: '레시피를 찾을 수 없습니다.' });
    }
    
    const recipe = recipes[0];
    
    // 재료 정보 가져오기
    const [ingredients] = await db.query(
      `SELECT name, amount, unit FROM recipe_ingredients WHERE recipe_id = ? ORDER BY id`,
      [id]
    );
    recipe.ingredients = ingredients.map(i => i.name);
    recipe.amounts = ingredients.map(i => `${i.amount || ''} ${i.unit || ''}`.trim());
    
    // 조리 단계 가져오기
    const [steps] = await db.query(
      `SELECT title, description, image_url FROM recipe_steps 
       WHERE recipe_id = ? ORDER BY step_order`,
      [id]
    );
    recipe.steps = steps;
    
    // 태그 가져오기
    const [tags] = await db.query(
      `SELECT rt.name FROM recipe_tags rt
       JOIN recipe_tag_relations rtr ON rt.id = rtr.tag_id
       WHERE rtr.recipe_id = ?`,
      [id]
    );
    recipe.tags = tags.map(t => t.name);
    
    // 팁 가져오기
    const [tips] = await db.query(
      `SELECT tip_text FROM recipe_tips WHERE recipe_id = ?`,
      [id]
    );
    recipe.tips = tips.map(t => t.tip_text);
    
    // 영양 정보 구조화
    recipe.nutrition = {
      calories: recipe.calories,
      protein: recipe.protein,
      carbs: recipe.carbs,
      fat: recipe.fat
    };
    
    // 상세 영양 정보 추가 (있을 경우)
    const [nutritionDetails] = await db.query(
      `SELECT sugar, sodium, fiber, cholesterol 
       FROM recipe_nutrition WHERE recipe_id = ?`,
      [id]
    );
    
    if (nutritionDetails.length > 0) {
      Object.assign(recipe.nutrition, nutritionDetails[0]);
    }
    
    // 응답에서 중복 필드 제거
    delete recipe.calories;
    delete recipe.protein;
    delete recipe.carbs;
    delete recipe.fat;
    
    // 조회수 증가 (선택적)
    await db.query(`UPDATE recipes SET view_count = view_count + 1 WHERE id = ?`, [id]);
    
    res.json(recipe);
  } catch (error) {
    console.error('레시피 상세 조회 중 오류:', error);
    res.status(500).json({ error: '서버 오류가 발생했습니다.' });
  }
};

// 연관 레시피 가져오기
exports.getRelatedRecipes = async (req, res) => {
  try {
    const { id } = req.params;
    
    // 현재 레시피의 카테고리와 태그 가져오기
    const [recipeInfo] = await db.query(
      `SELECT category FROM recipes WHERE id = ?`,
      [id]
    );
    
    if (recipeInfo.length === 0) {
      return res.status(404).json({ error: '레시피를 찾을 수 없습니다.' });
    }
    
    const category = recipeInfo[0].category;
    
    const [tags] = await db.query(
      `SELECT rt.id FROM recipe_tags rt
       JOIN recipe_tag_relations rtr ON rt.id = rtr.tag_id
       WHERE rtr.recipe_id = ?`,
      [id]
    );
    
    const tagIds = tags.map(t => t.id);
    
    // 연관 레시피 조회 (같은 카테고리 또는 같은 태그 기준)
    let query = `
      SELECT DISTINCT
        r.id, 
        r.name, 
        r.description, 
        r.image_url,
        r.calories
      FROM recipes r
      LEFT JOIN recipe_tag_relations rtr ON r.id = rtr.recipe_id
      WHERE r.id != ? AND (
        r.category = ?
    `;
    
    const params = [id, category];
    
    if (tagIds.length > 0) {
      query += ` OR rtr.tag_id IN (?)`;
      params.push(tagIds);
    }
    
    query += `) ORDER BY r.likes DESC LIMIT 3`;
    
    const [relatedRecipes] = await db.query(query, params);
    
    res.json(relatedRecipes);
  } catch (error) {
    console.error('연관 레시피 조회 중 오류:', error);
    res.status(500).json({ error: '서버 오류가 발생했습니다.' });
  }
};

// 좋아요 상태 조회
exports.getLikeStatus = async (req, res) => {
  try {
    // 사용자 인증 검사
    if (!req.user) {
      return res.json({ liked: false });
    }
    
    const { id } = req.params;
    const userId = req.user.id;
    
    const [likes] = await db.query(
      `SELECT * FROM recipe_likes WHERE user_id = ? AND recipe_id = ?`,
      [userId, id]
    );
    
    res.json({ liked: likes.length > 0 });
  } catch (error) {
    console.error('좋아요 상태 조회 중 오류:', error);
    res.status(500).json({ error: '서버 오류가 발생했습니다.' });
  }
};

// 좋아요 추가
exports.addLike = async (req, res) => {
  try {
    // 사용자 인증 검사
    if (!req.user) {
      return res.status(401).json({ error: '로그인이 필요합니다.' });
    }
    
    const { id } = req.params;
    const userId = req.user.id;
    
    // 이미 좋아요 했는지 확인
    const [likes] = await db.query(
      `SELECT * FROM recipe_likes WHERE user_id = ? AND recipe_id = ?`,
      [userId, id]
    );
    
    if (likes.length > 0) {
      return res.status(400).json({ error: '이미 좋아요를 눌렀습니다.' });
    }
    
    // 트랜잭션 시작
    await db.beginTransaction();
    
    // 좋아요 추가
    await db.query(
      `INSERT INTO recipe_likes (user_id, recipe_id) VALUES (?, ?)`,
      [userId, id]
    );
    
    // 레시피 좋아요 수 증가
    await db.query(
      `UPDATE recipes SET likes = likes + 1 WHERE id = ?`,
      [id]
    );
    
    await db.commit();
    
    res.json({ success: true });
  } catch (error) {
    await db.rollback();
    console.error('좋아요 추가 중 오류:', error);
    res.status(500).json({ error: '서버 오류가 발생했습니다.' });
  }
};

// 좋아요 취소
exports.removeLike = async (req, res) => {
  try {
    // 사용자 인증 검사
    if (!req.user) {
      return res.status(401).json({ error: '로그인이 필요합니다.' });
    }
    
    const { id } = req.params;
    const userId = req.user.id;
    
    // 이미 좋아요 했는지 확인
    const [likes] = await db.query(
      `SELECT * FROM recipe_likes WHERE user_id = ? AND recipe_id = ?`,
      [userId, id]
    );
    
    if (likes.length === 0) {
      return res.status(400).json({ error: '좋아요를 누르지 않았습니다.' });
    }
    
    // 트랜잭션 시작
    await db.beginTransaction();
    
    // 좋아요 삭제
    await db.query(
      `DELETE FROM recipe_likes WHERE user_id = ? AND recipe_id = ?`,
      [userId, id]
    );
    
    // 레시피 좋아요 수 감소
    await db.query(
      `UPDATE recipes SET likes = GREATEST(likes - 1, 0) WHERE id = ?`,
      [id]
    );
    
    await db.commit();
    
    res.json({ success: true });
  } catch (error) {
    await db.rollback();
    console.error('좋아요 취소 중 오류:', error);
    res.status(500).json({ error: '서버 오류가 발생했습니다.' });
  }
}; 