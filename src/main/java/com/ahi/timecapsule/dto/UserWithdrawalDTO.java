package com.ahi.timecapsule.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithdrawalDTO {
  @NotEmpty(message = "ID는 필수 입력값입니다.")
  private String id;

  private String password;
}
