package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Story;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
  private List<FindImageResponseDTO> images;
  private List<FindSharedResponseDTO> sharedStories;

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
            .images(story.getImages().stream()
                    .map(FindImageResponseDTO::fromEntity)
                    .toList())
            .sharedStories(story.getStoryShares().stream()
                    .map(FindSharedResponseDTO::fromEntity)
                    .toList())
            .build();
  }
}