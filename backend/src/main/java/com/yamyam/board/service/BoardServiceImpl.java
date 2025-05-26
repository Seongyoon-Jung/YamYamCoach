package com.yamyam.board.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yamyam.board.dto.BoardRequest;
import com.yamyam.board.dto.BoardResponse;
import com.yamyam.comment.dto.CommentResponse;
import com.yamyam.board.entity.BoardEntity;
import com.yamyam.comment.entity.CommentEntity;
import com.yamyam.user.entity.UserEntity;
import com.yamyam.board.repository.BoardRepository;
import com.yamyam.comment.repository.CommentRepository;
import com.yamyam.user.repository.UserRepository;

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
					board.getUser().getProfileUrl(),
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
								board.getUser().getProfileUrl(),
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
	public void modifyBoard(BoardRequest boardRequest, String imageUrl) {
		BoardEntity boardEntity = boardRepository.findByBoardId(boardRequest.getBoardId());
		
		boardEntity.setTitle(boardRequest.getTitle());
		boardEntity.setContent(boardRequest.getContent());
		
		if(imageUrl != null) {
			boardEntity.setImageUrl(imageUrl);
		}
		else {
			boardEntity.setImageUrl(boardRequest.getImageUrl());
		}
		
		boardRepository.save(boardEntity);
	}
	
	@Override
	public void deleteBoard(int boardId) {
		
		boardRepository.deleteById(boardId);
		
	}
}
