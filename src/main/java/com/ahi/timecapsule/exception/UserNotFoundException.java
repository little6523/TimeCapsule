package com.ahi.timecapsule.exception;

public class UserNotFoundException extends NotFoundException {
  private static final String DEFAULT_MESSAGE = "사용자를 찾을 수 없습니다.";

  public UserNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

  public UserNotFoundException(String message) {
    super(DEFAULT_MESSAGE + "\n" + message);
  }

  public UserNotFoundException(int userId) {
    super(DEFAULT_MESSAGE + " ID: " + userId);
  }
}
