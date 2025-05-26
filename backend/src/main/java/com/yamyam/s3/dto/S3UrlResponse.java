package com.yamyam.s3.dto;

public class S3UrlResponse {
	private String preSignedUrl;
    private String key;
    
    public S3UrlResponse() {
	}
    
	public S3UrlResponse(String preSignedUrl, String key) {
		this.preSignedUrl = preSignedUrl;
		this.key = key;
	}

	public String getPreSignedUrl() {
		return preSignedUrl;
	}

	public void setPreSignedUrl(String preSignedUrl) {
		this.preSignedUrl = preSignedUrl;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
    
	
}
