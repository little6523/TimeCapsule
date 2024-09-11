package com.ahi.timecapsule.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryOptionDTO {

  private String soundFile; // 인터뷰 파일

  private String dialect; // 사투리

  private String speaker; // 화자
}
