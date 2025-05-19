package com.yamyam.dto.request;

import java.util.List;

public class ClovaRequest {
    private String version;
    private String requestId;
    private long timestamp;
    private List<ImageInfo> images;
    private boolean enableTableDetection;
    
    public ClovaRequest() {
		// TODO Auto-generated constructor stub
	}

	public ClovaRequest(String version, String requestId, long timestamp, List<ImageInfo> images,
			boolean enableTableDetection) {
		this.version = version;
		this.requestId = requestId;
		this.timestamp = timestamp;
		this.images = images;
		this.enableTableDetection = enableTableDetection;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<ImageInfo> getImages() {
		return images;
	}

	public void setImages(List<ImageInfo> images) {
		this.images = images;
	}

	public boolean isEnableTableDetection() {
		return enableTableDetection;
	}

	public void setEnableTableDetection(boolean enableTableDetection) {
		this.enableTableDetection = enableTableDetection;
	}

	@Override
	public String toString() {
		return "ClovaRequest [version=" + version + ", requestId=" + requestId + ", timestamp=" + timestamp
				+ ", images=" + images.toString() + ", enableTableDetection=" + enableTableDetection + "]";
	}
}
