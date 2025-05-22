package com.yamyam.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yamyam.dto.request.CommentRequest;
import com.yamyam.dto.response.BoardResponse;
import com.yamyam.dto.response.CommentResponse;
import com.yamyam.entity.BoardEntity;
import com.yamyam.entity.CommentEntity;
import com.yamyam.entity.UserEntity;
import com.yamyam.repository.BoardRepository;
import com.yamyam.repository.CommentRepository;
import com.yamyam.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService{
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;
	public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository,
			BoardRepository boardRepository) {
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.boardRepository = boardRepository;
	}

	@Override
	public List<CommentResponse> getAllComment(int boardId) {
		List<CommentEntity> list = commentRepository.findAllByBoard_BoardIdOrderByCreatedAtDesc(boardId);
		List<CommentResponse> data = new ArrayList<>();
		
		for (CommentEntity comment : list) {
			data.add(new CommentResponse(
					comment.getCommentId(),
					comment.getUser().getUsername(),
					comment.getUser().getProfileUrl(),
					comment.getContent(),
					comment.getCreatedAt(),
					comment.getUpdatedAt()
					));
		}
		
		return data;
	}

	@Override
	public CommentResponse save(CommentRequest request) {
		UserEntity userEntity = userRepository.findByUsername(request.getUsername());
		BoardEntity boardEntity = boardRepository.findByBoardId(request.getBoardId());
		
		CommentEntity data = new CommentEntity(boardEntity, userEntity,request.getContent());
		
		
		CommentEntity commentEntity = commentRepository.save(data);
		CommentResponse commentResponse = new CommentResponse(commentEntity.getCommentId() ,request.getUsername(),userEntity.getProfileUrl(), request.getContent(), commentEntity.getCreatedAt(), commentEntity.getUpdatedAt());
		
		
		return commentResponse;
	}

	@Override
	public CommentResponse modify(CommentRequest request) {
		UserEntity userEntity = userRepository.findByUsername(request.getUsername());
		CommentEntity commentEntity = commentRepository.findByCommentId(request.getCommentId());
		
		commentEntity.setContent(request.getContent());
		
		CommentEntity data = commentRepository.save(commentEntity);
		
		CommentResponse commentResponse = new CommentResponse(data.getCommentId() ,request.getUsername(), userEntity.getProfileUrl(), request.getContent(), commentEntity.getCreatedAt(), commentEntity.getUpdatedAt());
		
		return commentResponse;
	}

	@Override
	public void delete(CommentRequest request) {
		commentRepository.deleteById(request.getCommentId());
	}
}
