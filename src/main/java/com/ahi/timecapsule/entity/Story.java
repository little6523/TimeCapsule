package com.ahi.timecapsule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Story {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 50)
  private String title;

  @Lob
  @Column(nullable = false)
  private String content;

  @Column
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private String soundFile;

  @Column(nullable = false)
  private boolean isShared;

  @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StoryShare> storyShares = new ArrayList<>();

  @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images = new ArrayList<>();
}
