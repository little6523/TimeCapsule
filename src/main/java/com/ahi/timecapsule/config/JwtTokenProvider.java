package com.ahi.timecapsule.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
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
    byte[] keyBytes = Base64.getDecoder().decode(key);  // Base64 인코딩된 키를 디코딩
    return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());  // HS512 알고리즘을 위한 키 생성
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

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(key).build().parseClaimsJws(authToken);

      // 토큰이 유효할 때 true 반환
      return true;
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      System.out.println("JWT 토큰이 만료되었습니다: " + e.getMessage());
    } catch (io.jsonwebtoken.MalformedJwtException e) {
      System.out.println("잘못된 JWT 토큰 형식입니다: " + e.getMessage());
    } catch (io.jsonwebtoken.UnsupportedJwtException e) {
      System.out.println("지원되지 않는 JWT 토큰입니다: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("JWT 토큰이 비어있습니다: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("JWT 검증 중 알 수 없는 오류 발생: " + e.getMessage());
    }
    // 토큰이 유효하지 않을 때 false 반환
    return false;
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    System.out.println("혹시?" + request);
    System.out.println("token이다" + bearerToken);
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
}
