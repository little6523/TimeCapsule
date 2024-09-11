package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.CommentDTO;
import com.ahi.timecapsule.entity.Comment;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.repository.CommentRepository;
import com.ahi.timecapsule.repository.StoryRepository;
import com.ahi.timecapsule.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
  private final CommentRepository commentRepository;
  private final StoryRepository storyRepository;
  private final UserRepository userRepository;

  @Autowired
  public CommentService(
      CommentRepository commentRepository,
      StoryRepository storyRepository,
      UserRepository userRepository) {
    this.commentRepository = commentRepository;
    this.storyRepository = storyRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<CommentDTO> getCommentsByStoryId(Long storyId) {
    List<Comment> comments = commentRepository.findByStoryId(storyId);
    return comments.stream().map(CommentDTO::fromComment).collect(Collectors.toList());
  }

  @Transactional
  public CommentDTO createComment(CommentDTO commentDTO) {
    User user =
        userRepository
            .findById(commentDTO.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
    Story story =
        storyRepository
            .findById(commentDTO.getStoryId())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 스토리 ID입니다."));

    Comment comment = commentDTO.toEntity(user, story);
    Comment savedComment = commentRepository.save(comment);
    return CommentDTO.fromComment(savedComment);
  }

  @Transactional
  public CommentDTO updateComment(Long commentId, CommentDTO commentDTO, String userId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 댓글입니다."));

    if (!comment.getUser().getUserId().equals(userId)) {
      throw new IllegalArgumentException("작성자만 본인 댓글을 수정할 수 있습니다.");
    }

    comment.updateContent(commentDTO.getContent());
    Comment updatedComment = commentRepository.save(comment);
    return CommentDTO.fromComment(updatedComment);
  }

  @Transactional
  public void deleteComment(Long commentId, String userId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 댓글입니다."));

    if (!comment.getUser().getUserId().equals(userId)) {
      throw new IllegalArgumentException("작성자만 본인 댓글을 삭제할 수 있습니다.");
    }

    commentRepository.delete(comment);
  }
}
