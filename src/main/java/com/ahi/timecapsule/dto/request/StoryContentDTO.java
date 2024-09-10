package com.ahi.timecapsule.dto.request;

import com.ahi.timecapsule.dto.ImageDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryContentDTO {

  private Long userId;

  private String title;

  private String content;

  private boolean isShared;

  private List<String> sharedUsers;

  private List<ImageDTO> imageFiles;

  @Override
  public String toString() {
    return "StoryContentDTO{" +
            "userId=" + userId +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", isShared=" + isShared +
            ", sharedUsers=" + sharedUsers +
            ", imageFiles=" + imageFiles +
            '}';
  }
}
