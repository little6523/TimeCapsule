package com.ahi.timecapsule.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
  @NotEmpty(message = "ID는 필수 입력값입니다.")
  private String id;

  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#!~$%^&*()_+=<>?])?\\S{6,20}$",
      message = "비밀번호는 대문자, 소문자, 숫자 중 2가지 이상을 포함한 6-20자리여야 합니다.")
  private String password;

  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#!~$%^&*()_+=<>?])?\\S{6,20}$",
      message = "새 비밀번호는 대문자, 소문자, 숫자 중 2가지 이상을 포함한 6-20자리여야 합니다.")
  private String newPassword;

  @NotEmpty(message = "닉네임은 필수 입력값입니다.")
  private String nickname;

  @NotEmpty(message = "이메일은 필수 입력값입니다.")
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;
}
