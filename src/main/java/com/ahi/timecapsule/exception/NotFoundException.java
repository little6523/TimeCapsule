package com.ahi.timecapsule.exception;

public class NotFoundException extends RuntimeException {

  public NotFoundException() {}

  public NotFoundException(String message) {
    super(message);
  }
}
