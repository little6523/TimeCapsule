package com.ahi.timecapsule.exception;

public class UserNotFoundException extends NotFoundException {
  private static final String DEFAULT_MESSAGE = "사용자를 찾을 수 없습니다.";

  public UserNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

  public UserNotFoundException(String userNickname) {
    super("사용자를 찾을 수 없습니다." + userNickname);
  }

  //  public UserNotFoundException(String userId) {
  //    super(DEFAULT_MESSAGE + " ID: " + userId);
  //  }
}
