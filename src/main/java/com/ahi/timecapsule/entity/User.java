package com.ahi.timecapsule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
  @Id
  @Column(name = "id")
  private String userId;

  private String password;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String nickname;

  private Integer role;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

//  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//  private List<Story> stories = new ArrayList<>();
//
//  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//  private List<Notice> notices = new ArrayList<>();
//
//  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//  private List<Comment> comments = new ArrayList<>();

  @Builder
  public User(String userId, String password, String email, String nickname, Integer role, Integer sharelistId) {
    this.userId = userId;
    this.password = password;
    this.email = email;
    this.nickname = nickname;
    this.role = role;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();

  }

}
