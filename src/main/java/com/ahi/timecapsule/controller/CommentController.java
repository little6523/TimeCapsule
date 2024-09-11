package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.CommentDTO;
import com.ahi.timecapsule.dto.UserDTO;
import com.ahi.timecapsule.service.CommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
  public ResponseEntity<CommentDTO> createComment(
      @RequestBody CommentDTO commentDto, Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDTO)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserDTO userDTO = (UserDTO) authentication.getPrincipal();
    commentDto.setUserId(userDTO.getUserId());
    try {
      CommentDTO createdComment = commentService.createComment(commentDto);
      return ResponseEntity.ok(createdComment);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<CommentDTO> updateComment(
      @PathVariable Long id, @RequestBody CommentDTO commentDto, Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDTO)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserDTO userDTO = (UserDTO) authentication.getPrincipal();
    try {
      CommentDTO updatedComment = commentService.updateComment(id, commentDto, userDTO.getUserId());
      return ResponseEntity.ok(updatedComment);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDTO)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserDTO userDTO = (UserDTO) authentication.getPrincipal();
    try {
      commentService.deleteComment(id, userDTO.getUserId());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
