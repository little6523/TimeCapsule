package com.ahi.timecapsule.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String userId) {
    super("사용자 아이디를 찾을 수 없습니다." +  userId );
  }
}