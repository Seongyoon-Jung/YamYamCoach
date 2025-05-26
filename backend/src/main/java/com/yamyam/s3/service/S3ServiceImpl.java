package com.yamyam.s3.service;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

@Service
public class S3ServiceImpl implements S3Service{
	private final AmazonS3 amazonS3;
    public S3ServiceImpl(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

	@Value("${aws-s3-bucket}")
    private String bucket;

	@Override
	public String generatePutPresignedUrl(String fileName) {
	    return generateUrl(fileName, HttpMethod.PUT);
	}

	@Override
	public String generateGetPresignedUrl(String fileName) {
		if (!amazonS3.doesObjectExist(bucket, fileName)) {
	        throw new IllegalArgumentException("파일이 존재하지 않습니다: " + fileName);
	    }
	    return generateUrl(fileName, HttpMethod.GET);
	}
	
	private String generateUrl(String fileName, HttpMethod method) {
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60* 5 ); // 5분
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(method)
                .withExpiration(expiration);
        
        return amazonS3.generatePresignedUrl(request).toString();
    }
}
