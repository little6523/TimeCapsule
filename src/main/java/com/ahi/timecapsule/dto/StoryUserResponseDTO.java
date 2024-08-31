package com.ahi.timecapsule.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryUserResponseDTO {
  private String id;
  private String nickname;
}
