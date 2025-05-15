package com.yamyam.dto.response;

import java.time.LocalDateTime;

public class CommentResponse {
//	board_id, user_id, content, created_at, updated_at
	private int commentId;
	private String userName;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public CommentResponse() {
	}

	public CommentResponse(int commentId, String userName, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.commentId = commentId;
		this.userName = userName;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
	
}
