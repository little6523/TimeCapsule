package com.ahi.timecapsule.dto.request;

import com.ahi.timecapsule.dto.ImageDTO;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryContentDTO {

  private String userId;

  private String title;

  private String content;

  private boolean isShared;

  private List<String> sharedUsers;

  private List<ImageDTO> imageFiles;

  @Override
  public String toString() {
    return "StoryContentDTO{"
        + "userId="
        + userId
        + ", title='"
        + title
        + '\''
        + ", content='"
        + content
        + '\''
        + ", isShared="
        + isShared
        + ", sharedUsers="
        + sharedUsers
        + ", imageFiles="
        + imageFiles
        + '}';
  }
}
