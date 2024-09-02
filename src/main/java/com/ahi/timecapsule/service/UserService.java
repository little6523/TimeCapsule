package com.ahi.timecapsule.service;

import com.ahi.timecapsule.config.JwtTokenProvider;
import com.ahi.timecapsule.config.PasswordEncoderConfig;
import com.ahi.timecapsule.dto.UserLoginDTO;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ahi.timecapsule.dto.UserDTO;
import com.ahi.timecapsule.dto.UserSignUpDTO;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoderConfig passwordencoderconfig;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

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

    //User 객체로 빌딩
    User user = User.builder()
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
    System.out.println("service옴");
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userlogindto.getUserId(), userlogindto.getPassword()));
    System.out.println("1");
    SecurityContextHolder.getContext().setAuthentication(authentication);
    System.out.println("여기까지");
    System.out.println(jwtTokenProvider.generateToken(authentication));
    return jwtTokenProvider.generateToken(authentication);


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
