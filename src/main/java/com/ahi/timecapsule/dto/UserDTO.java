package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDTO {
  private String id;
  private String password;
  private String email;
  private String nickname;
  private boolean role;

  public User toEntity() {
    return User.builder()
            .id(id)
            .password(password)
            .email(email)
            .nickname(nickname)
            .role(role)
            .build();
  }

  public static UserDTO fromEntity(User user) {
    return UserDTO.builder()
            .id(user.getId())
            .password(user.getPassword())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .role(user.isRole())
            .build();
  }


}
