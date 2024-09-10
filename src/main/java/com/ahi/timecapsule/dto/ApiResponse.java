package com.ahi.timecapsule.dto;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
  private final boolean success;
  private final String message;
  private T data;

  // 데이터가 있는 경우 생성자
  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  // 데이터가 없는 경우 생성자
  public ApiResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }
}
