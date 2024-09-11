package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Story;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FindStoryResponseDTO {
  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private String soundFile;
  private boolean isShared;
  private StoryUserResponseDTO author;
  private List<ImageDTO> images;
  private List<StoryShareDTO> sharedStories;

  // Entity -> DTO 변환 메서드
  public static FindStoryResponseDTO fromEntity(Story story) {
    return FindStoryResponseDTO.builder()
        .id(story.getId())
        .title(story.getTitle())
        .content(story.getContent())
        .createdAt(story.getCreatedAt())
        .soundFile(story.getSoundFile())
        .isShared(story.isShared())
        .author(StoryUserResponseDTO.fromEntity(story.getUser()))
        .images(story.getImages().stream().map(ImageDTO::fromEntity).toList())
        .sharedStories(story.getStoryShares().stream().map(StoryShareDTO::fromEntity).toList())
        .build();
  }
}
