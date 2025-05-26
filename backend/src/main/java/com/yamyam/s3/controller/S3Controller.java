package com.yamyam.s3.controller;

import com.yamyam.user.entity.UserEntity;
import com.yamyam.user.repository.UserRepository;
import com.yamyam.s3.service.S3Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;
    private final UserRepository userRepository;

    public S3Controller(S3Service s3Service, UserRepository userRepository) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
    }

    // 업로드용 Presigned URL
    @GetMapping("/put-url")
    public ResponseEntity<String> getPutPresignedUrl(@RequestParam("fileName") String fileName) {
        String url = s3Service.generatePutPresignedUrl(fileName);
        return ResponseEntity.ok(url);
    }

    // 사용자 프로필 이미지 Presigned GET URL
    @GetMapping("/get-url")
    public ResponseEntity<String> getUserImageUrl(@RequestParam("filename") String filename) {
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

//        String profileUrl = user.getProfileUrl();

        // AWS 키 추출 (예: https://.../uploads/user/profile.jpg → uploads/user/profile.jpg)
//        String key = extractS3KeyFromUrl(profileUrl);

        String presignedUrl = s3Service.generateGetPresignedUrl(filename);
        return ResponseEntity.ok(presignedUrl);
    }

    // URL에서 S3 객체 키 추출
    private String extractS3KeyFromUrl(String url) {
        URI uri = URI.create(url);
        return uri.getPath().substring(1); // 맨 앞 '/' 제거
    }
}
