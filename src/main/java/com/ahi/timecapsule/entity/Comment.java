package com.ahi.timecapsule.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, length = 300)
  private String content;

  @ManyToOne
  @JoinColumn(name = "story_id")
  private Story story;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  //    생성시간과 수정시간을 현재시간으로 설정
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }
  // 수정시간을 현재시간으로 업데이트
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public void updateContent(String content) {
    this.content = content;
  }
}
