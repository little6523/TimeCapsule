package com.ahi.timecapsule.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "story_share")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryShare {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "story_id", nullable = false)
  private Story story;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}