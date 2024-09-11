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
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "story_id", nullable = false)
  private Story story;

  @Column private String url;

  /**
   * Story 객체 설정 (내부에서만 사용) 편의 메서드를 통해서만 호출되어야 함.
   *
   * @param story Story 객체 (null = 관계 해제)
   */
  void setStoryInternal(Story story) {
    this.story = story;
  }
}
