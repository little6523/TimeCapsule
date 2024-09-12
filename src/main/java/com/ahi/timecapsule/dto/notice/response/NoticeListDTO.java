package com.ahi.timecapsule.dto.notice.response;

import com.ahi.timecapsule.entity.Notice;
import java.time.LocalDateTime;
import lombok.*;

// Notice 목록 조회를 위한 DTO
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeListDTO {

  private Long id;
  private String title;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String userNickname;

  // Entity -> DTO 변환
  public static NoticeListDTO fromEntity(Notice notice) {
    return NoticeListDTO.builder()
        .id(notice.getId())
        .title(notice.getTitle())
        .createdAt(notice.getCreatedAt())
        .updatedAt(notice.getUpdatedAt())
        .userNickname(notice.getUser().getNickname())
        .build();
  }
}
