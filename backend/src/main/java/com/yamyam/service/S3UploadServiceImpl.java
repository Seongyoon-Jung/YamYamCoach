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
	public void deleteImage(String imageUrl)  {
		if (imageUrl == null || imageUrl.isBlank()) return;

	    // 이미지 URL에서 S3 key 추출
	    // 예: https://your-bucket.s3.amazonaws.com/uploads/board/abc123.png
	    String key = imageUrl.substring(imageUrl.indexOf("uploads/"));

	    System.out.println("삭제할 S3 키: " + key);

	    amazonS3.deleteObject(bucket, key);
	}
}