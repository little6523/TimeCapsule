package com.ahi.timecapsule.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FindImageResponseDTO {
  private int id;
  private FindStoryResponseDTO story;
  private String url;
}
