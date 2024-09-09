package com.ahi.timecapsule.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String userNickname) {
    super("사용자를 찾을 수 없습니다." + userNickname);
  }
}
