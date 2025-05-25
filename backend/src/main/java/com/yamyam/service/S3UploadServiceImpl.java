package com.yamyam.service;

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
            return null;
        }
        
        // 파일명 생성 (UUID + 원본 파일명)
        String uuid = UUID.randomUUID().toString();
        String fileName = "uploads/recipe/" + uuid + "-" + file.getOriginalFilename();
        
        // S3에 업로드
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        
        return fileName; // S3 key 반환
    }
	
	@Override
	public void deleteImage(String imageUrl)  {
		if (imageUrl == null || imageUrl.isBlank()) return;

	    // 이미지 URL에서 S3 key 추출
	    // 예: https://your-bucket.s3.amazonaws.com/uploads/board/abc123.png
	    String key = imageUrl.substring(imageUrl.indexOf("uploads/"));

	    System.out.println("삭제할 S3 키: " + key);

	    amazonS3.deleteObject(bucket, key);
	}
}