package com.yamyam.recipe.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yamyam.auth.dto.SecurityAccount;
import com.yamyam.recipe.dto.RecipeRequest;
import com.yamyam.recipe.dto.RecipeResponse;
import com.yamyam.recipe.repository.RecipeRepository;
import com.yamyam.recipe.repository.RecipeLikeRepository;
import com.yamyam.recipe.service.RecipeService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final ObjectMapper objectMapper;
    private final RecipeRepository recipeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    
    public RecipeController(RecipeService recipeService, ObjectMapper objectMapper, RecipeRepository recipeRepository, RecipeLikeRepository recipeLikeRepository) {
        this.recipeService = recipeService;
        this.objectMapper = objectMapper;
        this.recipeRepository = recipeRepository;
        this.recipeLikeRepository = recipeLikeRepository;
    }

    // 레시피 생성
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @RequestPart("recipeData") String recipeDataJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        
        try {
            // 사용자 ID 가져오기
            Long userId = headerUserId;
            
            // 세션에서도 시도
            if (userId == null) {
                // 스프링 시큐리티 인증 객체에서 사용자 정보 가져오기
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && 
                    !authentication.getPrincipal().equals("anonymousUser")) {
                    SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                    userId = Long.valueOf(securityAccount.getUserId());
                } else {
                    // 기존 세션 방식 시도
                    Object sessionUserId = request.getSession().getAttribute("userId");
                    if (sessionUserId instanceof Long) {
                        userId = (Long) sessionUserId;
                    } else if (sessionUserId instanceof Integer) {
                        userId = ((Integer) sessionUserId).longValue();
                    }
                }
            }
            
            // 사용자 ID가 없으면 인증 실패
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // JSON 문자열을 RecipeRequest 객체로 변환
            RecipeRequest recipeRequest = objectMapper.readValue(recipeDataJson, RecipeRequest.class);
            
            RecipeResponse createdRecipe;
            if (mainImage != null && !mainImage.isEmpty()) {
                createdRecipe = recipeService.createRecipeWithImage(recipeRequest, mainImage, userId);
            } else {
                createdRecipe = recipeService.createRecipe(recipeRequest, userId);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 레시피 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id) {
        try {
            RecipeResponse recipe = recipeService.getRecipe(id);
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 레시피 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<RecipeResponse> recipes;
        if ((name != null && !name.isEmpty()) || (category != null && !category.isEmpty())) {
            recipes = recipeService.searchRecipes(name, category, pageable);
        } else {
            recipes = recipeService.getAllRecipes(pageable);
        }
        
        return ResponseEntity.ok(recipes);
    }

    // 카테고리별 레시피 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<RecipeResponse>> getRecipesByCategory(@PathVariable String category) {
        List<RecipeResponse> recipes = recipeService.getRecipesByCategory(category);
        return ResponseEntity.ok(recipes);
    }

    // 내 레시피 조회
    @GetMapping("/my-recipes")
    public ResponseEntity<List<RecipeResponse>> getMyRecipes(
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        
        // 사용자 ID 가져오기
        Long userId = headerUserId;
        
        // 세션에서도 시도
        if (userId == null) {
            // 스프링 시큐리티 인증 객체에서 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                userId = Long.valueOf(securityAccount.getUserId());
            } else {
                // 기존 세션 방식 시도
                Object sessionUserId = request.getSession().getAttribute("userId");
                if (sessionUserId instanceof Long) {
                    userId = (Long) sessionUserId;
                } else if (sessionUserId instanceof Integer) {
                    userId = ((Integer) sessionUserId).longValue();
                }
            }
        }
        
        // 사용자 ID가 없으면 인증 실패
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<RecipeResponse> recipes = recipeService.getRecipesByUserId(userId);
        return ResponseEntity.ok(recipes);
    }

    // 레시피 수정
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeRequest recipeRequest,
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        
        // 사용자 ID 가져오기
        Long userId = headerUserId;
        
        // 디버깅 로그 추가
        System.out.println("PUT /api/recipes/" + id + " 요청 받음");
        System.out.println("X-USER-ID 헤더: " + headerUserId);
        System.out.println("레시피 데이터: " + recipeRequest);
        
        // 세션에서도 시도
        if (userId == null) {
            // 스프링 시큐리티 인증 객체에서 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                userId = Long.valueOf(securityAccount.getUserId());
                System.out.println("시큐리티에서 찾은 사용자 ID: " + userId);
            } else {
                // 기존 세션 방식 시도
                Object sessionUserId = request.getSession().getAttribute("userId");
                if (sessionUserId instanceof Long) {
                    userId = (Long) sessionUserId;
                } else if (sessionUserId instanceof Integer) {
                    userId = ((Integer) sessionUserId).longValue();
                }
                System.out.println("세션에서 찾은 사용자 ID: " + userId);
            }
        }
        
        // 사용자 ID가 없으면 인증 실패
        if (userId == null) {
            System.out.println("사용자 ID를 찾을 수 없어 인증 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            RecipeResponse updatedRecipe = recipeService.updateRecipe(id, recipeRequest, userId);
            System.out.println("레시피 업데이트 성공: " + updatedRecipe.getId());
            return ResponseEntity.ok(updatedRecipe);
        } catch (Exception e) {
            System.out.println("레시피 업데이트 실패: " + e.getMessage());
            if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 이미지 포함 레시피 수정
    @PostMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipeWithImage(
            @PathVariable Long id,
            @RequestPart(value = "recipeData", required = true) String recipeDataJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        
        try {
            // 디버깅 로그 추가
            System.out.println("POST /api/recipes/" + id + " 요청 받음 (이미지 포함 수정)");
            System.out.println("X-USER-ID 헤더: " + headerUserId);
            System.out.println("recipeData JSON 길이: " + (recipeDataJson != null ? recipeDataJson.length() : "null"));
            System.out.println("mainImage 있음: " + (mainImage != null && !mainImage.isEmpty()));
            
            if (recipeDataJson == null || recipeDataJson.trim().isEmpty()) {
                System.err.println("recipeData가 비어 있습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            // 사용자 ID 가져오기
            Long userId = headerUserId;
            
            // 세션에서도 시도
            if (userId == null) {
                // 스프링 시큐리티 인증 객체에서 사용자 정보 가져오기
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && 
                    !authentication.getPrincipal().equals("anonymousUser")) {
                    SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                    userId = Long.valueOf(securityAccount.getUserId());
                } else {
                    // 기존 세션 방식 시도
                    Object sessionUserId = request.getSession().getAttribute("userId");
                    if (sessionUserId instanceof Long) {
                        userId = (Long) sessionUserId;
                    } else if (sessionUserId instanceof Integer) {
                        userId = ((Integer) sessionUserId).longValue();
                    }
                }
            }
            
            System.out.println("최종 사용자 ID: " + userId);
            
            // 사용자 ID가 없으면 인증 실패
            if (userId == null) {
                System.out.println("사용자 ID를 찾을 수 없어 인증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            // JSON 문자열을 RecipeRequest 객체로 변환
            RecipeRequest recipeRequest;
            try {
                recipeRequest = objectMapper.readValue(recipeDataJson, RecipeRequest.class);
                System.out.println("변환된 RecipeRequest: " + recipeRequest);
            } catch (Exception e) {
                System.err.println("JSON 변환 오류: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            // 레시피 업데이트
            RecipeResponse updatedRecipe;
            try {
                if (mainImage != null && !mainImage.isEmpty()) {
                    System.out.println("이미지 포함 업데이트 진행: 이미지 크기=" + mainImage.getSize() + " bytes");
                    updatedRecipe = recipeService.updateRecipeWithImage(id, recipeRequest, mainImage, userId);
                    System.out.println("이미지 포함 레시피 업데이트 성공: " + updatedRecipe.getId());
                } else {
                    System.out.println("일반 업데이트 진행 (이미지 없음)");
                    updatedRecipe = recipeService.updateRecipe(id, recipeRequest, userId);
                    System.out.println("일반 레시피 업데이트 성공: " + updatedRecipe.getId());
                }
                
                return ResponseEntity.ok(updatedRecipe);
            } catch (Exception e) {
                System.err.println("레시피 업데이트 실패: " + e.getMessage());
                e.printStackTrace();
                if (e.getMessage() != null && e.getMessage().contains("권한이 없습니다")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            System.err.println("요청 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 레시피 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        
        // 사용자 ID 가져오기
        Long userId = headerUserId;
        
        // 세션에서도 시도
        if (userId == null) {
            // 스프링 시큐리티 인증 객체에서 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                userId = Long.valueOf(securityAccount.getUserId());
            } else {
                // 기존 세션 방식 시도
                Object sessionUserId = request.getSession().getAttribute("userId");
                if (sessionUserId instanceof Long) {
                    userId = (Long) sessionUserId;
                } else if (sessionUserId instanceof Integer) {
                    userId = ((Integer) sessionUserId).longValue();
                }
            }
        }
        
        // 사용자 ID가 없으면 인증 실패
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            recipeService.deleteRecipe(id, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 레시피 검색
    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponse>> searchRecipes(@RequestParam String name) {
        List<RecipeResponse> recipes = recipeService.searchRecipesByName(name);
        return ResponseEntity.ok(recipes);
    }
    
    // 레시피 좋아요 처리
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeRecipe(
            @PathVariable Long id,
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        // 사용자 ID 가져오기
        Long userId = headerUserId;
        // 세션에서도 시도 (생략)
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                userId = Long.valueOf(securityAccount.getUserId());
            } else {
                Object sessionUserId = request.getSession().getAttribute("userId");
                if (sessionUserId instanceof Long) {
                    userId = (Long) sessionUserId;
                } else if (sessionUserId instanceof Integer) {
                    userId = ((Integer) sessionUserId).longValue();
                }
            }
        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            recipeService.likeRecipe(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    // 레시피 좋아요 취소
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeRecipe(
            @PathVariable Long id,
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        Long userId = headerUserId;
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                userId = Long.valueOf(securityAccount.getUserId());
            } else {
                Object sessionUserId = request.getSession().getAttribute("userId");
                if (sessionUserId instanceof Long) {
                    userId = (Long) sessionUserId;
                } else if (sessionUserId instanceof Integer) {
                    userId = ((Integer) sessionUserId).longValue();
                }
            }
        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            recipeService.unlikeRecipe(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // 레시피 좋아요 상태 확인
    @GetMapping("/{id}/like-status")
    public ResponseEntity<Object> getLikeStatus(
            @PathVariable Long id,
            @RequestHeader(value = "X-USER-ID", required = false) Long headerUserId,
            HttpServletRequest request) {
        // 사용자 ID 가져오기
        Long userId = headerUserId;
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                SecurityAccount securityAccount = (SecurityAccount) authentication.getPrincipal();
                userId = Long.valueOf(securityAccount.getUserId());
            } else {
                Object sessionUserId = request.getSession().getAttribute("userId");
                if (sessionUserId instanceof Long) {
                    userId = (Long) sessionUserId;
                } else if (sessionUserId instanceof Integer) {
                    userId = ((Integer) sessionUserId).longValue();
                }
            }
        }
        
        boolean liked = false;
        if (userId != null) {
            liked = recipeLikeRepository.findByRecipeIdAndUserId(id, userId).isPresent();
        }
        
        // 실제 좋아요 수 카운트 (NaN 방지)
        int likes = recipeLikeRepository.countByRecipeId(id);
        
        // 레시피 엔티티의 likes 필드도 함께 업데이트
        recipeRepository.findById(id).ifPresent(recipe -> {
            if (recipe.getLikes() == null || recipe.getLikes() != likes) {
                recipe.setLikes(likes);
                recipeRepository.save(recipe);
            }
        });
        
        return ResponseEntity.ok(java.util.Map.of("liked", liked, "likes", likes));
    }
} 