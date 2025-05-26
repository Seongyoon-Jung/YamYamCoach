package com.yamyam.s3.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3UploadService {	
	public String uploadImage(MultipartFile file) throws IOException;
	public void deleteImage(String filename);
}
