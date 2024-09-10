package com.ahi.timecapsule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {
  @NotBlank(message = "아이디를 입력해주세요.")
  private String userId;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  private String password;

  protected UserLoginDTO() {}

  public static UserLoginDTO createEmpty() {
    return new UserLoginDTO();
  }
}
