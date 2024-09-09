package com.ahi.timecapsule.config;

// ID와 비밀번호의 형식을 지정해놓은 정규식 함수
public class RegExp {
  // 완화된 비밀번호 정규식: 최소 6~20자, 대문자, 소문자, 숫자 중 두 가지 이상 포함, 특수 문자는 선택
  public static final String PASSWORD_REGEXP =
      "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#!~$%^&*()_+=<>?])?\\S{6,20}$";

  // 완화된 계정 ID 정규식: 4~20자의 알파벳 대소문자와 숫자
  public static final String ACCOUNT_ID_REGEXP = "^[A-Za-z0-9]{4,20}$";
}
