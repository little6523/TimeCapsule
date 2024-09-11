package com.ahi.timecapsule.exception;

public class NoticeNotFoundException extends RuntimeException {

  public NoticeNotFoundException() {
    super("해당 공지사항은 존재하지 않습니다.");
  }
}
