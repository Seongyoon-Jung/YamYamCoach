package com.yamyam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yamyam.dto.request.ClovaRequest;
import com.yamyam.util.MultipartInputStreamFileResource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ClovaServiceImpl implements ClovaService {

    @Value("${clova-secret-key}")
    private String secretKey;

    @Value("${clova-invoke-url}")
    private String invokeUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String requestClovaOCR(MultipartFile file, ClovaRequest payload) {
    	System.out.println("Invoke URL: " + invokeUrl);
    	System.out.println("Secret Key: " + secretKey);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", secretKey);

            // FormData 구성
            var body = new LinkedMultiValueMap<String, Object>();

            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            body.add("message", objectMapper.writeValueAsString(payload));

            HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(invokeUrl, requestEntity, String.class);

            // 전체 응답 로그 출력
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Headers: " + response.getHeaders());
            System.out.println("Body: " + response.getBody());
            
            return response.getBody();
        } catch (IOException e) {
            throw new RuntimeException("Clova OCR 요청 중 오류 발생", e);
        }
    }
}
