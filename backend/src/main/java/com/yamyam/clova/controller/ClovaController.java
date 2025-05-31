package com.yamyam.clova.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yamyam.clova.dto.ClovaRequest;
import com.yamyam.clova.service.ClovaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/clova")
public class ClovaController {

	private final ClovaService clovaService;
    private final ObjectMapper objectMapper;

    public ClovaController(ClovaService clovaService, ObjectMapper objectMapper) {
        this.clovaService = clovaService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("")
    public ResponseEntity<?> handleOcrForm(@RequestParam("file") MultipartFile file, @RequestParam("message") String message) throws JsonMappingException, JsonProcessingException {
        ClovaRequest payload = objectMapper.readValue(message, ClovaRequest.class);
        String response = clovaService.requestClovaOCR(file, payload);
        return ResponseEntity.ok(response);
    }
}