package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Comment;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.User;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentDTO {
  private Long id;
  private long storyId;
  private String userId;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String userNickname;

  // Entity -> DTO
  public static CommentDTO fromComment(Comment comment) {
    return CommentDTO.builder()
        .id(comment.getId())
        .content(comment.getContent())
        .userId(comment.getUser().getUserId())
        .userNickname(comment.getUser().getNickname())
        .storyId(comment.getStory().getId())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }

  // DTO -> Entity
  public Comment toEntity(User user, Story story) {
    return Comment.builder()
        .content(this.content)
        .user(user)
        .story(story)
        .createdAt(this.createdAt)
        .updatedAt(this.updatedAt)
        .build();
  }
}
