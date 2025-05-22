package com.yamyam.dto.request;

public class CommentRequest {
	private int commentId;
	private int boardId;
	private String username;
	private String content;
	
	public CommentRequest() {
	}

	public CommentRequest(int commentId, int boardId, String username, String content) {
		this.commentId = commentId;
		this.boardId = boardId;
		this.username = username;
		this.content = content;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "CommentRequest [commentId=" + commentId + ", boardId=" + boardId + ", username=" + username
				+ ", content=" + content + "]";
	}

	
}
