package com.ahi.timecapsule.dto;

import static com.ahi.timecapsule.config.RegExp.ACCOUNT_ID_REGEXP;
import static com.ahi.timecapsule.config.RegExp.PASSWORD_REGEXP;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpDTO {
  @NotBlank(message = "아이디는 필수 입력값입니다.")
  @Pattern(message = "대소문자나 숫자를 포함한 4-20자리로 입력하세요.", regexp = ACCOUNT_ID_REGEXP)
  private String userId;

  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Pattern(message = "알파벳과 숫자를 포함한 6-20자리의 비밀번호를 입력하세요.", regexp = PASSWORD_REGEXP)
  private String password;

  private String confirmPassword;

  @NotBlank(message = "닉네임을 입력해주세요")
  private String nickname;

  @NotBlank(message = "이메일을 입력해주세요")
  @Email(message = "이메일 형식으로 입력해주세요.")
  private String email;

  protected UserSignUpDTO() {}

  public static UserSignUpDTO createEmpty() {
    return new UserSignUpDTO();
  }
}
