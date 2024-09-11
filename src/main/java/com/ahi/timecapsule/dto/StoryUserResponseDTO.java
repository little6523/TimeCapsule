package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryUserResponseDTO {
  private String id;
  private String nickname;

  // Entity -> DTO 변환 메서드
  public static StoryUserResponseDTO fromEntity(User user) {
    return StoryUserResponseDTO.builder().id(user.getUserId()).nickname(user.getNickname()).build();
  }
}
