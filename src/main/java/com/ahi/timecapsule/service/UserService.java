package com.ahi.timecapsule.service;

import com.ahi.timecapsule.config.AuthorityUtils;
import com.ahi.timecapsule.config.JwtTokenProvider;
import com.ahi.timecapsule.config.PasswordEncoderConfig;
import com.ahi.timecapsule.config.RedisService;
import com.ahi.timecapsule.dto.UserDTO;
import com.ahi.timecapsule.dto.UserLoginDTO;
import com.ahi.timecapsule.dto.UserSignUpDTO;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoderConfig passwordencoderconfig;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;

  private final Integer role_user = 1;

  @Transactional
  public UserDTO registerUser(UserSignUpDTO usersignupdto) {
    // 중복 검사
    if (existsByUserId(usersignupdto.getUserId())) {
      throw new DataIntegrityViolationException("이미 존재하는 사용자 ID입니다.");
    }
    if (existsByEmail(usersignupdto.getEmail())) {
      throw new DataIntegrityViolationException("이미 존재하는 이메일입니다.");
    }
    if (existsByNickname(usersignupdto.getNickname())) {
      throw new DataIntegrityViolationException("이미 존재하는 닉네임입니다.");
    }
    // 비밀번호 암호화
    String password = passwordencoderconfig.passwordEncoder().encode(usersignupdto.getPassword());

    // User 객체로 빌딩
    User user =
        User.builder()
            .userId(usersignupdto.getUserId())
            .email(usersignupdto.getEmail())
            .nickname(usersignupdto.getNickname())
            .password(password)
            .role(role_user)
            .build();

    return UserDTO.fromEntity(userRepository.save(user));
  }

  @Transactional
  public Boolean isPasswordMatching(UserSignUpDTO usersignupdto) {
    return usersignupdto.getPassword().equals(usersignupdto.getConfirmPassword());
  }

  @Transactional
  public String login(UserLoginDTO userlogindto) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userlogindto.getUserId(), userlogindto.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    System.out.println("authentication " + authentication);
    String accessToken = jwtTokenProvider.generateAccessToken(authentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
    System.out.println("accessToken 발급" + accessToken);

    // Refresh Token을 Redis에 저장 (유효 기간 설정)
    redisService.saveRefreshToken(userlogindto.getUserId(), refreshToken);
    System.out.println("redis에 저장");
    return accessToken;
  }

  // Access Token 검증 및 필요한 경우 자동 재발급 메서드
  public String validateAndRefreshAccessToken(String accessToken, String refreshToken) {
    // Access Token 유효성 검사
    System.out.println(jwtTokenProvider.validateToken(accessToken));
    if (jwtTokenProvider.validateToken(accessToken)) {
      // Access Token이 유효하면 기존 토큰 반환
      return accessToken;
    }

    // Access Token이 만료된 경우, Refresh Token 검사
    if (jwtTokenProvider.validateToken(refreshToken)) {
      String username = jwtTokenProvider.getUsernameFromJwtToken(refreshToken);

      // Redis에서 Refresh Token 검증
      if (redisService.validateRefreshToken(username, refreshToken)) {
        // User 객체 가져오기
        User user =
            userRepository
                .findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 숫자 권한을 GrantedAuthority로 변환
        List<GrantedAuthority> authorities =
            AuthorityUtils.convertRolesToAuthorities(user.getRole());

        // Authentication 객체 생성
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(user, null, authorities);

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        // 새로운 Refresh Token 발급 및 저장 (선택 사항)
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        redisService.saveRefreshToken(username, newRefreshToken);

        // 새로운 Access Token 반환
        return newAccessToken;
      }
    }

    // Refresh Token이 유효하지 않거나 만료된 경우 예외 처리 또는 재로그인 요구
    throw new RuntimeException("유효하지 않은 토큰입니다. 다시 로그인하세요.");
  }

  @Transactional
  public boolean isDuplicate(String field, String value) {
    switch (field) {
      case "userId":
        return existsByUserId(value);
      case "email":
        return existsByEmail(value);
      case "nickname":
        return existsByNickname(value);
      default:
        throw new IllegalArgumentException("잘못된 필드 이름: " + field);
    }
  }

  @Transactional
  public boolean existsByUserId(String userId) {
    return userRepository.existsById(userId);
  }

  @Transactional
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Transactional
  public boolean existsByNickname(String nickname) {
    return userRepository.existsByNickname(nickname);
  }
}
