package com.ahi.timecapsule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UpdateStoryRequestDTO {

  private String title;
  private String content;
  private List<String> images;
  private List<String> sharedWithUsers;
  @JsonProperty("isShared")
  private boolean isShared;

}
