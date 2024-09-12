package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.CommentDTO;
import com.ahi.timecapsule.service.CommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
  private final CommentService commentService;

  @Autowired
  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping("/story/{storyId}")
  public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long storyId) {
    try {
      List<CommentDTO> comments = commentService.getCommentsByStoryId(storyId);
      return ResponseEntity.ok(comments);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping
  public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDto) {

    if (commentDto.getUserId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      CommentDTO createdComment = commentService.createComment(commentDto);
      return ResponseEntity.ok(createdComment);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<CommentDTO> updateComment(
      @PathVariable Long id, @RequestBody CommentDTO commentDto) {

    if (commentDto.getUserId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      CommentDTO updatedComment =
          commentService.updateComment(id, commentDto, commentDto.getUserId());
      return ResponseEntity.ok(updatedComment);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(
      @PathVariable Long id, @RequestHeader("User-Id") String userId // 헤더에서 User-Id 값을 추출
      ) {
    try {
      // userId와 commentId를 이용하여 댓글 삭제 처리
      commentService.deleteComment(id, userId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제에 실패했습니다.");
    }
  }
}
