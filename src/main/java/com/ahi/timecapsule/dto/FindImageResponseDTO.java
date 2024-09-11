package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Image;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FindImageResponseDTO {
  private Long id;
  private FindStoryResponseDTO story;
  private String url;

  // Entity -> DTO 변환 메서드
  public static FindImageResponseDTO fromEntity(Image image) {
    return FindImageResponseDTO.builder().id(image.getId()).url(image.getUrl()).build();
  }
}
