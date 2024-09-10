package com.ahi.timecapsule.config;

import com.ahi.timecapsule.oauth.CustomOAuth2User;
import com.ahi.timecapsule.oauth.GoogleOAuth2UserInfo;
import com.ahi.timecapsule.oauth.KakaoOAuth2UserInfo;
import com.ahi.timecapsule.oauth.OAuth2UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  // Refresh Token 저장
  public void saveRefreshToken(String username, String refreshToken) {
    // 키를 username으로 하고, 토큰을 값으로 저장, 예: 7일간 유효
    redisTemplate.opsForValue().set("refreshToken:" + username, refreshToken, Duration.ofDays(7));
  }

  // Refresh Token 조회 메서드
  public String getRefreshToken(String username) {
    // Redis에서 사용자 이름을 키로 사용하여 Refresh Token을 조회
    return redisTemplate.opsForValue().get("refreshToken:" + username);
  }

  // Refresh Token 검증
  public boolean validateRefreshToken(String username, String refreshToken) {
    String storedToken = redisTemplate.opsForValue().get("refreshToken:" + username);
    return refreshToken.equals(storedToken);
  }

  // Refresh Token 삭제 (로그아웃 시)
  public void deleteRefreshToken(String username) {
    redisTemplate.delete("refreshToken:" + username);
  }

  // Access Token을 블랙리스트에 추가
  public void addToBlacklist(String accessToken) {
    // 토큰 만료 시간을 기준으로 블랙리스트에 저장
    long expiration = jwtTokenProvider.getExpiration(accessToken);
    redisTemplate
        .opsForValue()
        .set("blacklist:" + accessToken, "true", Duration.ofMillis(expiration));
  }

  // Access Token이 블랙리스트에 있는지 확인
  public boolean isBlacklisted(String accessToken) {
    return redisTemplate.hasKey("blacklist:" + accessToken);
  }

  // 일회성 코드의 key로  OAuth2 사용자 정보와 제공자 정보를 Redis에 저장
  public void saveOneTimeCode(
      String oneTimeCode, CustomOAuth2User oAuth2User, String provider, long expirationTime) {
    try {
      Map<String, Object> userMap = new HashMap<>();
      userMap.put("provider", provider);
      userMap.put("attributes", oAuth2User.getAttributes());

      String userJson = objectMapper.writeValueAsString(userMap);
      redisTemplate
          .opsForValue()
          .set("OTC:" + oneTimeCode, userJson, expirationTime, TimeUnit.SECONDS);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize CustomOAuth2User", e);
    }
  }

  // Redis에서 일회성 코드로 사용자 정보를 가져오고, 해당 코드를 삭제한 후 CustomOAuth2User 객체로 반환
  public CustomOAuth2User getUserAndDeleteOneTimeCode(String oneTimeCode) {
    String key = "OTC:" + oneTimeCode;
    String userJson = redisTemplate.opsForValue().get(key);
    if (userJson != null) {
      redisTemplate.delete(key);
      try {
        Map<String, Object> userMap = objectMapper.readValue(userJson, Map.class);
        String provider = (String) userMap.get("provider");
        Map<String, Object> attributes = (Map<String, Object>) userMap.get("attributes");

        OAuth2UserInfo userInfo;
        switch (provider) {
          case "kakao":
            userInfo = new KakaoOAuth2UserInfo(attributes);
            break;
          case "google":
            userInfo = new GoogleOAuth2UserInfo(attributes);
            break;
          default:
            throw new IllegalArgumentException("Unknown provider: " + provider);
        }

        return new CustomOAuth2User(userInfo);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to deserialize user data", e);
      }
    }
    return null;
  }
}
