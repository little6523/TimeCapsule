package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.StoryShare;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FindSharedResponseDTO {
  private int id;
  private FindStoryResponseDTO sharedStory;
  private StoryUserResponseDTO sharedWithUser;

  // Entity -> DTO 변환 메서드
  public static FindSharedResponseDTO fromEntity(StoryShare storyShare) {
    return FindSharedResponseDTO.builder()
            .id(storyShare.getId())
            .sharedWithUser(StoryUserResponseDTO.fromEntity(storyShare.getUser()))
            .build();
  }
}
