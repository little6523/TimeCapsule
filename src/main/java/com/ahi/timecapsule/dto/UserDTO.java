package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

  public static UserDTO fromEntity(User user) {
    return UserDTO.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .password(user.getPassword())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();

  }
}
