package com.ahi.timecapsule.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "story_id", nullable = false)
  private Story story;

  @Column
  private String url;

  /**
   * Story 객체를 내부적으로 설정합니다.
   * 이 메서드는 편의 메서드를 통해서만 호출되어야 하며,
   * 외부에서는 직접 호출하지 말아야 합니다.
   *
   * @param story Story 객체 (null 값은 관계 해제를 의미합니다)
   */
  void setStoryInternal(Story story) {
    this.story = story;
  }

}