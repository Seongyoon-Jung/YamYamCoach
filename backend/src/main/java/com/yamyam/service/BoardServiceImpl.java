package com.yamyam.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yamyam.dto.request.BoardRequest;
import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;
import com.yamyam.entity.BoardEntity;
import com.yamyam.entity.CommentEntity;
import com.yamyam.entity.UserEntity;
import com.yamyam.repository.BoardRepository;
import com.yamyam.repository.CommentRepository;
import com.yamyam.repository.UserRepository;

@Service
public class BoardServiceImpl implements BoardService{
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	public BoardServiceImpl(BoardRepository boardRepository, CommentRepository commentRepository, UserRepository userRepository) {
		this.boardRepository = boardRepository;
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<BoardResponse> getAll() {

		List<BoardEntity> list = boardRepository.findAllByOrderByCreatedAtDesc();
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
	public void plusViewCount(int boardId) {
		BoardEntity board = boardRepository.findByBoardId(boardId);
		board.setViewCount(board.getViewCount()+1);
		
		boardRepository.save(board);
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
	public void writeBoard(BoardRequest boardRequest) {
		
		UserEntity userEntity = userRepository.findByUsername(boardRequest.getUsername());
		
		BoardEntity data = new BoardEntity(userEntity, boardRequest.getTitle(), boardRequest.getContent(), boardRequest.getImageUrl());
		
		boardRepository.save(data);
	}

	@Override
	public void modifyBoard(BoardRequest boardRequest) {
		BoardEntity boardEntity = boardRepository.findByBoardId(boardRequest.getBoardId());
		
		boardEntity.setTitle(boardRequest.getTitle());
		boardEntity.setContent(boardRequest.getContent());
		boardEntity.setImageUrl(boardRequest.getImageUrl());
		
		boardRepository.save(boardEntity);
	}
	
	@Override
	public void deleteBoard(int boardId) {
		
		boardRepository.deleteById(boardId);
		
	}
}
