package com.yamyam.diet.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yamyam.entity.PersonaEntity;
import com.yamyam.repository.PersonaRepository;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {
    
    private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);
    
    @Autowired
    private PersonaRepository personaRepository;
    
    /**
     * 특정 페르소나 ID로 페르소나 정보 조회
     */
    @GetMapping("/{personaId}")
    public ResponseEntity<?> getPersona(@PathVariable Integer personaId) {
        try {
            logger.info("페르소나 ID [{}] 조회 요청", personaId);
            
            Optional<PersonaEntity> personaOpt = personaRepository.findById(personaId);
            if (personaOpt.isPresent()) {
                PersonaEntity persona = personaOpt.get();
                
                Map<String, Object> personaInfo = new HashMap<>();
                personaInfo.put("personaId", persona.getPersonaId());
                personaInfo.put("name", persona.getName());
                personaInfo.put("diseaseTags", persona.getDiseaseTags());
                personaInfo.put("description", persona.getDescription());
                personaInfo.put("recommendation", persona.getRecommendation());
                
                logger.info("페르소나 정보 조회 성공: {}", personaInfo);
                return ResponseEntity.ok(Map.of("success", true, "persona", personaInfo));
            } else {
                logger.warn("페르소나 ID [{}]에 해당하는 페르소나 정보를 찾을 수 없습니다", personaId);
                return ResponseEntity.ok(Map.of("success", false, "message", "페르소나 정보를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            logger.error("페르소나 정보 조회 중 오류 발생", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "페르소나 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 