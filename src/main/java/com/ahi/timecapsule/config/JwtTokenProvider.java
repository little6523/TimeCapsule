package com.ahi.timecapsule.config;

import com.ahi.timecapsule.oauth.CustomOAuth2User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  //  private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  @Value("${jwt.jwtSecret}")
  private String key;

  @Value("${jwt.accessTokenExpirationMs}")
  private int accessTokenExpirationMs;

  @Value("${jwt.refreshTokenExpirationMs}")
  private long refreshTokenExpirationMs;

  private Key getSigningKey() {
    byte[] keyBytes = Base64.getDecoder().decode(key); // Base64 인코딩된 키를 디코딩
    return new SecretKeySpec(
        keyBytes, SignatureAlgorithm.HS512.getJcaName()); // HS512 알고리즘을 위한 키 생성
  }

  public String generateAccessToken(Authentication authentication) {
    User userPrincipal = (User) authentication.getPrincipal();

    String authorities =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .claim("roles", authorities) // 권한 정보 포함
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + accessTokenExpirationMs))
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        //                .signWith(jwtSecret)
        .compact();
  }

  public String generateAccessToken(String userId, List<GrantedAuthority> roles) {
    // 권한 목록을 문자열로 변환
    String authorities =
        roles.stream()
            .map(GrantedAuthority::getAuthority) // 각 권한의 이름을 가져옴
            .collect(Collectors.joining(","));

    // JWT 토큰 생성
    return Jwts.builder()
        .setSubject(userId) // userId를 서브젝트로 설정
        .claim("roles", authorities) // 권한 정보 포함
        .setIssuedAt(new Date()) // 발급 시간 설정
        .setExpiration(new Date((new Date()).getTime() + accessTokenExpirationMs)) // 만료 시간 설정
        .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명 알고리즘 및 키 설정
        .compact(); // 토큰 생성
  }

  public String generateRefreshToken(Authentication authentication) {
    User userPrincipal = (User) authentication.getPrincipal();

    return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(
            new Date((new Date()).getTime() + refreshTokenExpirationMs)) // Refresh Token 유효 기간
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  public String generateRefreshToken(String userId) {

    // JWT 토큰 생성
    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(new Date())
        .setExpiration(
            new Date((new Date()).getTime() + refreshTokenExpirationMs)) // Refresh Token 유효 기간
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    try {
      // 만료되지 않은 토큰에서 사용자 이름을 추출
      return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    } catch (ExpiredJwtException e) {
      // 만료된 토큰에서 사용자 이름을 추출
      return e.getClaims().getSubject();
    }
  }

  public String getAuthoritiesFromJwtToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.get("roles", String.class);
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(key).build().parseClaimsJws(authToken);
      // 토큰이 유효할 때 true 반환
      return true;
    } catch (ExpiredJwtException
        | MalformedJwtException
        | SignatureException
        | UnsupportedJwtException
        | IllegalArgumentException e) {
      // 필요한 경우 예외 로그 처리
      return false;
    }
    // 토큰이 유효하지 않을 때 false 반환

  }

  // Refresh 토큰의 유효성을 검증하는 메서드
  private boolean isValidRefreshToken(String refreshToken) {
    try {
      Jwts.parser().setSigningKey(key).build().parseClaimsJws(refreshToken);
      return true; // Refresh 토큰이 유효할 때 true 반환
    } catch (JwtException e) {
      return false; // Refresh 토큰이 유효하지 않으면 false 반환
    }
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    //        return bearerToken;
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
    }
    return null;
  }

  public long getExpiration(String token) {
    Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.getExpiration().getTime() - System.currentTimeMillis();
  }

  // OAuth2.0 로그인 시 AccessToken 생성
  public String generateOAuth2AccessToken(CustomOAuth2User oAuth2User) {
    String authorities =
        oAuth2User.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(oAuth2User.getId())
        .claim("roles", authorities)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + accessTokenExpirationMs))
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  // OAuth2.0 로그인 시 RefreshToken 생성
  public String generateOAuth2RefreshToken(CustomOAuth2User oAuth2User) {
    return Jwts.builder()
        .setSubject(oAuth2User.getId())
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + refreshTokenExpirationMs))
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }
}
