package com.yamyam.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;
import com.yamyam.entity.BoardEntity;
import com.yamyam.entity.CommentEntity;
import com.yamyam.repository.BoardRepository;
import com.yamyam.repository.CommentRepository;

@Service
public class BoardServiceImpl implements BoardService{
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	public BoardServiceImpl(BoardRepository boardRepository, CommentRepository commentRepository) {
		this.boardRepository = boardRepository;
		this.commentRepository = commentRepository;
	}

	@Override
	public List<BoardResponse> getAll() {
		List<BoardEntity> list = boardRepository.findAll();
		List<BoardResponse> data = new ArrayList<>();
		
		for (BoardEntity board : list) {
			data.add(new BoardResponse(
					board.getBoardId(),
					board.getUser().getUsername(),
					board.getTitle(),
					board.getContent(),
					board.getCreatedAt(),
					board.getUpdatedAt(),
					board.getViewCount(),
					board.getLikeCount(),
					board.getImageUrl()
					));
		}
		
		return data;
	}

	@Override
	public BoardResponse getBoard(int boardId) {
		BoardEntity board = boardRepository.findByBoardId(boardId);
		
		BoardResponse data = new BoardResponse(
								board.getBoardId(),
								board.getUser().getUsername(),
								board.getTitle(),
								board.getContent(),
								board.getCreatedAt(),
								board.getUpdatedAt(),
								board.getViewCount(),
								board.getLikeCount(),
								board.getImageUrl()
							);
		
		return data;
	}


	@Override
	public List<CommentResponse> getAllComment(int boardId) {
		
		List<CommentEntity> list = commentRepository.findAllByBoard_BoardIdOrderByCreatedAtDesc(boardId);
		List<CommentResponse> data = new ArrayList<>();
		
		for (CommentEntity comment : list) {
			data.add(new CommentResponse(
					comment.getUser().getUsername(),
					comment.getContent(),
					comment.getCreatedAt(),
					comment.getUpdatedAt()
					));
		}
		
		return data;
	}
}
