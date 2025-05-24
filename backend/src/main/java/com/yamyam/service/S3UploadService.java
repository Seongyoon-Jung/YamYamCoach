package com.yamyam.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3UploadService {	
	public void deleteImage(String filename);
}
