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
    public String saveFile(MultipartFile multipartFile, String where) throws IOException {
		String directory = "uploads/"+where+"/";
		
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        //파일의 이름이 겹치거나, 보안적으로 문제가 될수 있으므로 랜덤 uuid값으로 변경후 넣는다
        String savedName = directory + UUID.randomUUID().toString() + ext;
        
        
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

         amazonS3.putObject(bucket, savedName, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, savedName).toString();
    }
}