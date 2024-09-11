package com.ahi.timecapsule.exception;

public class StoryNotFoundException extends NotFoundException {

  private static final String DEFAULT_MESSAGE = "스토리를 찾을 수 없습니다.";

  public StoryNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

  public StoryNotFoundException(String message) {
    super(DEFAULT_MESSAGE + "\n" + message);
  }

  public StoryNotFoundException(Long storyId) {
    super(DEFAULT_MESSAGE + " ID: " + storyId);
  }
}
