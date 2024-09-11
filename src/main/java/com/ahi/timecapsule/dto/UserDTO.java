package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
  private String userId;
  private String password;
  private String email;
  private String nickname;
  private Integer role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String provider; // 추가

  public static UserDTO fromEntity(User user) {
    return UserDTO.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .password(user.getPassword())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .provider(user.getProvider()) // 추가
        .build();
  }

  public User toEntity() {
    return User.builder()
        .userId(userId)
        .email(email)
        .nickname(nickname)
        .password(password)
        .role(role)
        .build();
  }
}
