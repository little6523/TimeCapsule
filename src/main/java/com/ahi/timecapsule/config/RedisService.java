package com.ahi.timecapsule.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;

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
}
