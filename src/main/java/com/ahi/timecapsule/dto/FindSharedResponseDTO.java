package com.ahi.timecapsule.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FindSharedResponseDTO {
  private int id;
  private FindStoryResponseDTO sharedStory;
  private StoryUserResponseDTO sharedWithUser;
}
