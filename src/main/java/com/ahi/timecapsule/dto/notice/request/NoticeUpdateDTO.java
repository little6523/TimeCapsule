package com.ahi.timecapsule.dto.notice.request;

import com.ahi.timecapsule.entity.Notice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// Notice 수정을 위한 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateDTO {

  private Integer id;

  @NotBlank(message = "제목은 비워둘 수 없습니다.")
  @Size(min = 1, max = 50, message = "제목은 최소 1자, 최대 50자까지 허용됩니다.")
  private String title;

  @NotBlank(message = "내용은 비워둘 수 없습니다.")
  private String content;

  // DTO -> Entity 변환
  public Notice toEntity(Notice notice) {
    notice.updateNotice(this.title, this.content);
    return notice;
  }
}
