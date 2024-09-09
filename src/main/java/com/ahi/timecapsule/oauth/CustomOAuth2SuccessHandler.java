package com.ahi.timecapsule.oauth;

import com.ahi.timecapsule.config.JwtTokenProvider;
import com.ahi.timecapsule.config.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

    String accessToken = jwtTokenProvider.generateOAuth2AccessToken(oAuth2User);
    String refreshToken = jwtTokenProvider.generateOAuth2RefreshToken(oAuth2User);

    /*
    	1.일회용 코드 생성
    	2.일회용 코드 redis 서버에 저장 및 RefreshToken redis 서버에 저장
    	3.클라이언트로 리다이렉트(코드, 사용자 정보 담아서)
    	4.클라이언트에서 서버로 요청
    	5.서버에서 redis 서버에 저장된 일회용 코드 삭제
    	6.새로운 AccessToken, RefreshToken 생성
    	7.RefreshToken redis 서버에 저장
    	8.AccessToken 클라이언트 Local Storage 저장
    */
    // 일회용 코드 생성
    String oneTimeCode = generateOneTimeCode();

    // 제공자
    String provider = extractProviderFromAuthentication(authentication);

    // 리프레시 토큰을 Redis에 저장
    redisService.saveRefreshToken(oAuth2User.getId(), refreshToken);

    // 일회용 코드 저장(5분 동안 유지)
    redisService.saveOneTimeCode(oneTimeCode, oAuth2User, provider, 300);

    String targetUrl =
        UriComponentsBuilder.fromUriString("/login")
            .queryParam("code", oneTimeCode)
            .build()
            .toUriString();

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  // 고유한 일회성 코드 생성
  private String generateOneTimeCode() {
    return UUID.randomUUID().toString();
  }

  // OAuth2.0 제공자 추출
  private String extractProviderFromAuthentication(Authentication authentication) {
    if (authentication instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
      return oauthToken.getAuthorizedClientRegistrationId();
    }
    return "unknown";
  }
}
