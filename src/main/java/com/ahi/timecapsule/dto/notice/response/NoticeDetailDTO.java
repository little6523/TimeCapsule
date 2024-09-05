package com.ahi.timecapsule.dto.notice.response;

import com.ahi.timecapsule.entity.Notice;
import java.time.LocalDateTime;
import lombok.*;

// Notice 상세 조회를 위한 DTO
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeDetailDTO {

  private Integer id;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String userId;
  private String userNickname;

  // Entity -> DTO 변환
  public static NoticeDetailDTO fromEntity(Notice notice) {
    return NoticeDetailDTO.builder()
        .id(notice.getId())
        .title(notice.getTitle())
        .content(notice.getContent())
        .createdAt(notice.getCreatedAt())
        .updatedAt(notice.getUpdatedAt())
        .userId(notice.getUser().getUserId())
        .userNickname(notice.getUser().getNickname())
        .build();
  }
}
