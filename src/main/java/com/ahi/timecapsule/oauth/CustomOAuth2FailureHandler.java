package com.ahi.timecapsule.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    String errorMessage = "인증에 실패했습니다.";
    String errorCode = "authentication_error";

    // 이메일 중복 예외 처리
    if (exception instanceof EmailAlreadyExistsException) {
      errorMessage = exception.getMessage();
      errorCode = "email_exists";
    }

    String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
    String targetUrl =
        UriComponentsBuilder.fromUriString("/login")
            .queryParam("error", errorCode)
            .queryParam("message", encodedErrorMessage)
            .build()
            .toUriString();

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}
