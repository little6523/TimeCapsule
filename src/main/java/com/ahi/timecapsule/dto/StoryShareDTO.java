package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.StoryShare;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryShareDTO {

  private StoryDTO storyDTO;

  private UserDTO userDTO;

  public static StoryShareDTO fromEntity(StoryShare storyShare) {
    return StoryShareDTO.builder()
        .storyDTO(StoryDTO.fromEntity(storyShare.getStory()))
        .userDTO(UserDTO.fromEntity(storyShare.getUser()))
        .build();
  }

  public StoryShare toEntity() {
    return StoryShare.builder().story(storyDTO.toEntity()).user(userDTO.toEntity()).build();
  }
}
