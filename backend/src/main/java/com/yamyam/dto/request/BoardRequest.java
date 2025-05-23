package com.yamyam.dto.request;

public class BoardRequest {
	private int boardId;
	private String username;
	private String title;
	private String content;
	private String imageUrl;
	
	public BoardRequest() {
	}

	public BoardRequest(int boardId, String username, String title, String content, String imageUrl) {
		this.boardId = boardId;
		this.username = username;
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
 
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "BoardRequest [boardId=" + boardId + ", username=" + username + ", title=" + title + ", content="
				+ content + ", imageUrl=" + imageUrl + "]";
	}
	
	
	
	
}
