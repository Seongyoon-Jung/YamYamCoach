package com.yamyam.s3.service;

public interface S3Service {
	public String generatePutPresignedUrl(String fileName);
	public String generateGetPresignedUrl(String fileName);
}
