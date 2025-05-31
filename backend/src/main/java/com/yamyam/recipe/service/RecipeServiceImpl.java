package com.yamyam.recipe.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.yamyam.recipe.dto.RecipeRequest;
import com.yamyam.recipe.dto.RecipeResponse;
import com.yamyam.recipe.model.Recipe;
import com.yamyam.recipe.repository.RecipeRepository;
import com.yamyam.s3.service.S3Service;
import com.yamyam.s3.service.S3UploadService;
import com.yamyam.user.entity.UserEntity;
import com.yamyam.user.repository.UserRepository;
import com.yamyam.recipe.repository.RecipeLikeRepository;
import com.yamyam.recipe.model.RecipeLike;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final S3UploadService s3UploadService;
    private final RecipeLikeRepository recipeLikeRepository;
    
    public RecipeServiceImpl(RecipeRepository recipeRepository, 
                            UserRepository userRepository,
                            S3Service s3Service,
                            S3UploadService s3UploadService,
                            RecipeLikeRepository recipeLikeRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
        this.s3UploadService = s3UploadService;
        this.recipeLikeRepository = recipeLikeRepository;
    }

    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest recipeRequest, Long userId) {
        UserEntity user = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Recipe recipe = new Recipe();
        recipe.setName(recipeRequest.getName());
        recipe.setCategory(recipeRequest.getCategory());
        
        // 재료를 개행 문자로 구분하여 저장
        String ingredients = processIngredients(recipeRequest.getIngredients());
        recipe.setIngredients(ingredients);
        
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setContent(recipeRequest.getContent());
        recipe.setCookTimeMinutes(recipeRequest.getCookTimeMinutes());
        recipe.setDifficulty(recipeRequest.getDifficulty());
        recipe.setServings(recipeRequest.getServings());
        recipe.setImageUrl(recipeRequest.getImageUrl());
        recipe.setUser(user);
        recipe.setLikes(0);
        
        Recipe savedRecipe = recipeRepository.save(recipe);
        return new RecipeResponse(savedRecipe);
    }

    @Override
    @Transactional
    public RecipeResponse createRecipeWithImage(RecipeRequest recipeRequest, MultipartFile imageFile, Long userId) {
        if (imageFile == null || imageFile.isEmpty()) {
            return createRecipe(recipeRequest, userId);
        }

        try {
            // S3에 이미지 직접 업로드하고 키 받기
            String imageKey = s3UploadService.uploadImage(imageFile);
            
            // 이미지 URL 설정
            recipeRequest.setImageUrl(imageKey);
            
            return createRecipe(recipeRequest, userId);
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeResponse getRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
        
        syncLikesCount(recipe);
        
        RecipeResponse response = new RecipeResponse(recipe);
        
        // 이미지 URL이 있다면 presigned URL 생성
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            try {
                String presignedUrl = s3Service.generateGetPresignedUrl(recipe.getImageUrl());
                response.setImageUrl(presignedUrl);
            } catch (Exception e) {
                // 이미지 URL 생성 실패 시 원래 URL 유지
            }
        }
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(this::convertToResponseWithPresignedUrl)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getAllRecipes(Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(this::convertToResponseWithPresignedUrl);
    }

    @Override
    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest recipeRequest, Long userId) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
        
        // 권한 확인
        if (recipe.getUser().getUserId() != userId.intValue()) {
            throw new IllegalArgumentException("레시피를 수정할 권한이 없습니다.");
        }
        
        // 재료를 개행 문자로 구분하여 처리
        String ingredients = processIngredients(recipeRequest.getIngredients());
        
        recipe.update(
            recipeRequest.getName(),
            recipeRequest.getCategory(),
            ingredients, // 처리된 재료
            recipeRequest.getDescription(),
            recipeRequest.getContent(),
            recipeRequest.getCookTimeMinutes(),
            recipeRequest.getDifficulty(),
            recipeRequest.getServings(),
            recipeRequest.getImageUrl()
        );
        
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return convertToResponseWithPresignedUrl(updatedRecipe);
    }

    @Override
    @Transactional
    public RecipeResponse updateRecipeWithImage(Long id, RecipeRequest recipeRequest, MultipartFile imageFile, Long userId) {
        if (imageFile == null || imageFile.isEmpty()) {
            System.out.println("이미지가 없어 일반 업데이트 메서드 호출");
            return updateRecipe(id, recipeRequest, userId);
        }
        
        // 유효성 검사 추가
        if (recipeRequest == null) {
            throw new IllegalArgumentException("레시피 데이터가 없습니다.");
        }
        
        if (id == null || userId == null) {
            throw new IllegalArgumentException("레시피 ID 또는 사용자 ID가 유효하지 않습니다.");
        }
        
        // 레시피 조회
        Recipe recipe;
        try {
            recipe = recipeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다: ID=" + id));
        } catch (Exception e) {
            System.err.println("레시피 조회 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("레시피 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
        
        // 권한 확인
        if (recipe.getUser() == null || recipe.getUser().getUserId() != userId.intValue()) {
            System.err.println("권한 오류 - 레시피 작성자: " + (recipe.getUser() != null ? recipe.getUser().getUserId() : "null") + ", 요청자: " + userId);
            throw new IllegalArgumentException("레시피를 수정할 권한이 없습니다.");
        }
        
        try {
            System.out.println("이미지 포함 레시피 업데이트 - 레시피 ID: " + id + ", 사용자 ID: " + userId);
            System.out.println("현재 이미지 URL: " + recipe.getImageUrl());
            System.out.println("새 이미지 파일: " + imageFile.getOriginalFilename() + ", 크기: " + imageFile.getSize() + " bytes, ContentType: " + imageFile.getContentType());
            
            // 이미지 유효성 검사
            if (imageFile.getContentType() == null || !imageFile.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("유효한 이미지 파일이 아닙니다. (ContentType: " + imageFile.getContentType() + ")");
            }
            
            // 기존 이미지가 있다면 삭제 시도
            String oldImageUrl = null;
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                oldImageUrl = recipe.getImageUrl();
                System.out.println("기존 이미지 삭제 시도: " + oldImageUrl);
                try {
                    s3UploadService.deleteImage(oldImageUrl);
                    System.out.println("기존 이미지 삭제 성공");
                } catch (Exception e) {
                    System.err.println("기존 이미지 삭제 중 오류 발생 (계속 진행): " + e.getMessage());
                    // 이미지 삭제 실패해도 업데이트 계속 진행
                }
            }
            
            // 새 이미지 직접 업로드하고 키 받기
            String imageKey;
            try {
                imageKey = s3UploadService.uploadImage(imageFile);
                if (imageKey == null || imageKey.isEmpty()) {
                    throw new RuntimeException("이미지 업로드 후 빈 키가 반환되었습니다.");
                }
                System.out.println("새 이미지 업로드 완료 - 키: " + imageKey);
            } catch (Exception e) {
                System.err.println("새 이미지 업로드 중 오류 발생: " + e.getMessage());
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
            }
            
            // 이미지 URL 설정
            recipeRequest.setImageUrl(imageKey);
            
            // 일반 업데이트 메서드 호출 (레시피 정보 업데이트)
            return updateRecipe(id, recipeRequest, userId);
        } catch (Exception e) {
            System.err.println("이미지 업로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteRecipe(Long id, Long userId) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
        
        // 권한 확인
        if (recipe.getUser().getUserId() != userId.intValue()) {
            throw new IllegalArgumentException("레시피를 삭제할 권한이 없습니다.");
        }
        
        // 이미지가 있다면 삭제
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            s3UploadService.deleteImage(recipe.getImageUrl());
        }
        
        recipeRepository.delete(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getRecipesByUserId(Long userId) {
        List<Recipe> recipes = recipeRepository.findByUserId(userId);
        return recipes.stream()
                .map(this::convertToResponseWithPresignedUrl)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getRecipesByCategory(String category) {
        List<Recipe> recipes = recipeRepository.findByCategory(category);
        return recipes.stream()
                .map(this::convertToResponseWithPresignedUrl)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> searchRecipesByName(String name) {
        List<Recipe> recipes = recipeRepository.findByNameContaining(name);
        return recipes.stream()
                .map(this::convertToResponseWithPresignedUrl)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchRecipes(String name, String category, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.searchRecipes(name, category, pageable);
        return recipePage.map(this::convertToResponseWithPresignedUrl);
    }
    
    @Override
    @Transactional
    public void incrementLikes(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
        
        // 현재 좋아요 수 가져와서 1 증가
        Integer currentLikes = recipe.getLikes() != null ? recipe.getLikes() : 0;
        recipe.setLikes(currentLikes + 1);
        
        recipeRepository.save(recipe);
    }
    
    @Override
    @Transactional
    public void likeRecipe(Long recipeId, Long userId) {
        // 이미 좋아요를 눌렀는지 확인
        if (recipeLikeRepository.findByRecipeIdAndUserId(recipeId, userId).isPresent()) {
            return; // 이미 좋아요를 누른 경우 아무 작업도 하지 않음
        }
        
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
        
        // RecipeLike 테이블에 레코드 추가
        recipeLikeRepository.save(new RecipeLike(recipeId, userId));
        
        // Recipe 엔티티의 likes 필드 업데이트
        // NaN 방지를 위해 null 체크 추가
        Integer currentLikes = recipe.getLikes() != null ? recipe.getLikes() : 0;
        recipe.setLikes(currentLikes + 1);
        
        recipeRepository.save(recipe);
    }

    @Override
    @Transactional
    public void unlikeRecipe(Long recipeId, Long userId) {
        // 좋아요를 누른 적이 있는지 확인
        if (recipeLikeRepository.findByRecipeIdAndUserId(recipeId, userId).isEmpty()) {
            return; // 좋아요를 누른 적이 없는 경우 아무 작업도 하지 않음
        }
        
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
        
        // RecipeLike 테이블에서 레코드 삭제
        recipeLikeRepository.deleteByRecipeIdAndUserId(recipeId, userId);
        
        // Recipe 엔티티의 likes 필드 업데이트
        // NaN 방지를 위해 null 체크 추가 및 음수 방지
        Integer currentLikes = recipe.getLikes() != null ? recipe.getLikes() : 0;
        recipe.setLikes(Math.max(0, currentLikes - 1));
        
        recipeRepository.save(recipe);
    }
    
    // Recipe를 RecipeResponse로 변환하며 Presigned URL도 생성
    private RecipeResponse convertToResponseWithPresignedUrl(Recipe recipe) {
        RecipeResponse response = new RecipeResponse(recipe);
        
        // 이미지 URL이 있다면 presigned URL 생성
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            try {
                String presignedUrl = s3Service.generateGetPresignedUrl(recipe.getImageUrl());
                response.setImageUrl(presignedUrl);
            } catch (Exception e) {
                // 이미지 URL 생성 실패 시 원래 URL 유지
            }
        }
        
        return response;
    }

    @Transactional
    private void syncLikesCount(Recipe recipe) {
        if (recipe == null) return;
        
        // 실제 DB의 좋아요 수 조회
        int actualLikes = recipeLikeRepository.countByRecipeId(recipe.getId());
        
        // Recipe 엔티티의 likes 필드가 null이거나 실제 DB의 좋아요 수와 다르면 업데이트
        if (recipe.getLikes() == null || recipe.getLikes() != actualLikes) {
            recipe.setLikes(actualLikes);
            recipeRepository.save(recipe);
        }
    }

    // 재료 처리 메서드 - 콤마(,)와 개행 문자(\n)를 모두 구분자로 인식
    private String processIngredients(String ingredients) {
        if (ingredients == null || ingredients.trim().isEmpty()) {
            return "";
        }
        
        // 개행 문자와 콤마(,)를 모두 구분자로 인식
        // 1. 개행 문자로 먼저 분리
        String[] lines = ingredients.split("\\r?\\n");
        StringBuilder processedIngredients = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            
            // 비어 있지 않은 라인이고 콤마가 포함된 경우 콤마로 추가 분리
            if (!line.isEmpty()) {
                if (line.contains(",")) {
                    String[] commaItems = line.split(",");
                    for (String item : commaItems) {
                        String trimmedItem = item.trim();
                        if (!trimmedItem.isEmpty()) {
                            processedIngredients.append(trimmedItem).append("\n");
                        }
                    }
                } else {
                    // 콤마가 없는 경우 그대로 추가
                    processedIngredients.append(line).append("\n");
                }
            }
        }
        
        // 마지막 개행 문자 제거
        if (processedIngredients.length() > 0) {
            processedIngredients.setLength(processedIngredients.length() - 1);
        }
        
        return processedIngredients.toString();
    }
} 