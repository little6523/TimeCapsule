package com.ahi.timecapsule.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  // 중복 검사
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<String> handleDataIntegrityViolationException(
      DataIntegrityViolationException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler({BadCredentialsException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    return new ResponseEntity<>("알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
