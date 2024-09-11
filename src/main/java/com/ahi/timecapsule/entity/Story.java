package com.ahi.timecapsule.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "stories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Story extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 50)
  private String title;

  @Lob
  @Column(nullable = false)
  private String content;

  private String dialect;

  private String speaker;

  @Column(nullable = false)
  private String soundFile;

  @Column(nullable = false)
  private boolean isShared;

  @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<StoryShare> storyShares = new ArrayList<>();

  @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Image> images = new ArrayList<>();

  // 양방향 관계를 유지하는 편의 메서드
  public void addStoryShare(StoryShare storyShare) {
    if (!storyShares.contains(storyShare)) {
      storyShares.add(storyShare);
      storyShare.setStoryInternal(this); // StoryShare에 스토리 설정
    }
  }

  public void removeStoryShare(StoryShare storyShare) {
    if (storyShares.contains(storyShare)) {
      storyShares.remove(storyShare);
      storyShare.setStoryInternal(null); // StoryShare의 스토리 해제
    }
  }

  public void addImage(Image image) {
    if (!images.contains(image)) {
      images.add(image);
      image.setStoryInternal(this); // image에 스토리 추가
    }
  }

  public void removeImage(Image image) {
    if (images.contains(image)) {
      images.remove(image);
      image.setStoryInternal(null); // image의 스토리 해제
    }
  }

  // 스토리를 업데이트 하는 메서드
  public void updateStory(
      String newTitle, String newContent, Boolean newIsShared, List<StoryShare> newStoryShares) {
    this.title = newTitle != null ? newTitle : this.title;
    this.content = newContent != null ? newContent : this.content;
    this.isShared = newIsShared != null ? newIsShared : this.isShared;

    this.storyShares.clear();
    if (newStoryShares != null) {
      newStoryShares.forEach(this::addStoryShare);
    }
  }
}
