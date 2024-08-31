package com.ahi.timecapsule.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FindStoryResponseDTO {
  private int id;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private String soundFile;
  private boolean isShared;
  private StoryUserResponseDTO author;
  private List<FindImageResponseDTO> images;
  private List<FindSharedResponseDTO> sharedStories;
}