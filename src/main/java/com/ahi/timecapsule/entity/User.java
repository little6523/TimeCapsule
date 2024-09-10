package com.ahi.timecapsule.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

  @CreationTimestamp private LocalDateTime createdAt;

  @UpdateTimestamp private LocalDateTime updatedAt;

  @Column private String provider; // 추가

  //  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  //  private List<Story> stories = new ArrayList<>();
  //
  //  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  //  private List<Notice> notices = new ArrayList<>();
  //
  //  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  //  private List<Comment> comments = new ArrayList<>();

  @Builder
  public User(
      String userId,
      String password,
      String email,
      String nickname,
      Integer role,
      Integer sharelistId,
      String provider) { // 추가
    this.userId = userId;
    this.password = password;
    this.email = email;
    this.nickname = nickname;
    this.role = role;
    this.provider = provider; // 추가
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
}
