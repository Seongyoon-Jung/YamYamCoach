package com.yamyam.s3.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class S3UploadServiceImpl implements S3UploadService{

    private final AmazonS3 amazonS3;
    public S3UploadServiceImpl(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

	@Value("${aws-s3-bucket}")
    private String bucket;
    
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            System.err.println("uploadImage: 파일이 null이거나 비어 있습니다.");
            return null;
        }
        
        System.out.println("S3 이미지 업로드 시작: " + file.getOriginalFilename() + ", 크기: " + file.getSize() + " bytes, ContentType: " + file.getContentType());
        
        try {
            // 파일명 생성 (UUID + 원본 파일명)
            String uuid = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            
            // 원본 파일명이 null인 경우 처리
            if (originalFilename == null || originalFilename.isEmpty()) {
                originalFilename = "unknown-" + System.currentTimeMillis() + ".jpg";
                System.out.println("원본 파일명이 없어 기본값 사용: " + originalFilename);
            }
            
            // 특수문자, 공백 등 처리
            originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
            
            String fileName = "uploads/recipe/" + uuid + "-" + originalFilename;
            System.out.println("생성된 S3 키: " + fileName);
            
            // S3에 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            
            System.out.println("S3 업로드 준비: 버킷=" + bucket + ", 키=" + fileName);
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
            System.out.println("S3 업로드 완료: " + fileName);
            
            return fileName; // S3 key 반환
        } catch (Exception e) {
            System.err.println("S3 이미지 업로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("이미지 업로드 중 오류: " + e.getMessage(), e);
        }
    }
	
	@Override
	public void deleteImage(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) return;

		try {
			// 이미지 URL에서 S3 key 추출
			String key;
			if (imageUrl.contains("uploads/")) {
				// 경로 형태가 "uploads/..."인 경우
				key = imageUrl.substring(imageUrl.indexOf("uploads/"));
			} else if (imageUrl.startsWith("http")) {
				// 전체 URL인 경우 마지막 '/'부터 파일 이름 추출
				key = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
				// 경로 추가
				key = "uploads/recipe/" + key;
			} else {
				// 이미 키 형태인 경우 그대로 사용
				key = imageUrl;
			}

			System.out.println("삭제할 S3 키: " + key);
			amazonS3.deleteObject(bucket, key);
		} catch (Exception e) {
			// 삭제 실패해도 진행 (중요한 작업 아님)
			System.err.println("이미지 삭제 중 오류 발생 (무시됨): " + e.getMessage());
		}
	}
}