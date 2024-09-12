package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Image;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ImageDTO {

  private Long id;

  private String url;

  private StoryDTO storyDTO;

  public Image toEntity() {
    return Image.builder().story(storyDTO.toEntity()).url(url).build();
  }

  public static ImageDTO fromEntity(Image image) {
    return ImageDTO.builder()
        .id(image.getId())
        .storyDTO(StoryDTO.fromEntity(image.getStory()))
        .url(image.getUrl())
        .build();
  }
}
