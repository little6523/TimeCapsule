package com.ahi.timecapsule.service;

import com.ahi.timecapsule.config.*;
import com.ahi.timecapsule.dto.*;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.oauth.CustomOAuth2User;
import com.ahi.timecapsule.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
  private final EmailService emailService;

  private final Integer role_user = 1;

  private static final String CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // 대문자나 소문자, 숫자 중 2개를 포함한
  // 6-20자리
  private static final SecureRandom RANDOM = new SecureRandom(); // 임시 비밀번호 생성을 위한 랜덤 객체

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
    Optional<User> optionalUser = userRepository.findByUserId(userlogindto.getUserId());

    // 사용자가 없거나 비밀번호가 틀린 경우 예외 발생
    User user =
        optionalUser.orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 잘못되었습니다."));
    if (!passwordencoderconfig
        .passwordEncoder()
        .matches(userlogindto.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("아이디 또는 비밀번호가 잘못되었습니다.");
    }

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userlogindto.getUserId(), userlogindto.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String accessToken = jwtTokenProvider.generateAccessToken(authentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

    // Refresh Token을 Redis에 저장 (유효 기간 설정)
    redisService.saveRefreshToken(userlogindto.getUserId(), refreshToken);
    return accessToken;
  }

  // OAuth2.0 로그인
  @Transactional
  public TokenResponse oauthLogin(String oneTimeCode) {
    CustomOAuth2User oAuth2User = redisService.getUserAndDeleteOneTimeCode(oneTimeCode);
    if (oAuth2User == null) {
      return null;
    }

    String accessToken = jwtTokenProvider.generateOAuth2AccessToken(oAuth2User);
    String refreshToken = jwtTokenProvider.generateOAuth2RefreshToken(oAuth2User);

    redisService.saveRefreshToken(oAuth2User.getId(), refreshToken);
    return new TokenResponse(accessToken, refreshToken);
  }

  // Access Token 검증 및 필요한 경우 자동 재발급 메서드
  public String validateAndRefreshAccessToken(String accessToken, String refreshToken) {
    // Access Token 유효성 검사
    boolean isAccessTokenValid = jwtTokenProvider.validateToken(accessToken);

    if (isAccessTokenValid) {
      // Access Token이 유효하면 기존 토큰 반환
      return accessToken;
    }

    String username =
        jwtTokenProvider.getUsernameFromJwtToken(
            refreshToken); // refreshToken은 따로 검증 안함. 만료여부와 무관하게 재발급.

    // Redis에서 Refresh Token이 동일한지 검증
    if (redisService.validateRefreshToken(username, refreshToken)) {

      // User 객체 가져오기
      User user =
          userRepository
              .findByUserId(username)
              .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

      List<GrantedAuthority> role = AuthorityUtils.convertRolesToAuthorities(user.getRole());
      String newAccessToken = jwtTokenProvider.generateAccessToken(username, role);

      // 새로운 Refresh Token 발급 및 저장 (선택 사항)
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
      redisService.saveRefreshToken(username, newRefreshToken);

      // 새로운 Access Token 반환
      return newAccessToken;
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

  @Transactional(readOnly = true)
  public UserDTO getUserInfo(String id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ID가 있는 사용자를 찾을 수 없습니다 : " + id));
    return UserDTO.fromEntity(user);
  }

  @Transactional
  public ApiResponse<Void> issueTemporaryPassword(TemporaryPasswordDTO temporaryPasswordDTO) {
    try {
      User user =
          userRepository
              .findByUserIdAndEmail(temporaryPasswordDTO.getId(), temporaryPasswordDTO.getEmail())
              .orElseThrow(() -> new EntityNotFoundException("일치 하는 계정 정보가 없습니다."));

      // 임시 비밀번호 생성
      String tempPassword = createRandomPassword();

      User newUserPassword =
          User.builder()
              .userId(user.getUserId())
              .password(passwordencoderconfig.passwordEncoder().encode(tempPassword))
              .email(user.getEmail())
              .nickname(user.getNickname())
              .role(user.getRole())
              .build();

      // 임시 비밀번호 저장
      userRepository.save(newUserPassword);

      // 임시 비밀번호 이메일 전송
      emailService.sendTempPasswordEmail(user.getEmail(), tempPassword);
      return new ApiResponse<>(true, "임시 비밀번호가 이메일로 전송되었습니다.");
    } catch (EntityNotFoundException e) {
      return new ApiResponse<>(false, e.getMessage());
    } catch (MailException e) {
      return new ApiResponse<>(false, "임시 비밀번호 이메일 전송에 실패했습니다.");
    } catch (Exception e) {
      return new ApiResponse<>(false, "임시 비밀번호 발급 처리 중 오류가 발생했습니다.");
    }
  }

  @Transactional
  public ApiResponse<String> updateUser(UserUpdateDTO userUpdateDTO) {
    try {
      User user =
          userRepository
              .findById(userUpdateDTO.getId())
              .orElseThrow(() -> new EntityNotFoundException("요청하신 사용자 정보를 찾을 수 없습니다."));

      User.UserBuilder userUpdateBuilder =
          User.builder()
              .userId(user.getUserId())
              .password(user.getPassword())
              .email(userUpdateDTO.getEmail())
              .nickname(userUpdateDTO.getNickname())
              .provider(user.getProvider())
              .role(user.getRole());

      // 비밀번호 수정일 경우
      if (userUpdateDTO.getPassword() != null && userUpdateDTO.getNewPassword() != null) {
        // 사용자가 입력한 비밀번호와 DB의 비밀번호와 비교
        if (!passwordencoderconfig
            .passwordEncoder()
            .matches(userUpdateDTO.getPassword(), user.getPassword())) {
          return new ApiResponse<>(false, "기존 비밀번호가 일치하지 않습니다.");
        }
        userUpdateBuilder.password(
            passwordencoderconfig.passwordEncoder().encode(userUpdateDTO.getNewPassword()));
      }

      User updateUser = userUpdateBuilder.build();
      userRepository.save(updateUser);

      return new ApiResponse<>(true, "사용자 정보가 성공적으로 업데이트되었습니다.", updateUser.getUserId());
    } catch (EntityNotFoundException e) {
      return new ApiResponse<>(false, e.getMessage());
    } catch (Exception e) {
      return new ApiResponse<>(false, "사용자 정보 업데이트 중 오류가 발생했습니다.");
    }
  }

  @Transactional(readOnly = true)
  public boolean isNicknameDuplicate(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  @Transactional(readOnly = true)
  public boolean isEmailDuplicate(String email) {
    return userRepository.existsByEmail(email);
  }

  @Transactional
  public ApiResponse<Void> deleteUser(UserWithdrawalDTO userWithdrawalDTO) {
    try {
      User user =
          userRepository
              .findById(userWithdrawalDTO.getId())
              .orElseThrow(() -> new EntityNotFoundException("요청하신 사용자 정보를 찾을 수 없습니다."));

      if (user.getProvider() == null
          && !passwordencoderconfig
              .passwordEncoder()
              .matches(userWithdrawalDTO.getPassword(), user.getPassword())) {
        return new ApiResponse<>(false, "기존 비밀번호가 일치하지 않습니다.");
      }

      // 사용자 정보 삭제
      userRepository.delete(user);
      // refresh token redis 삭제
      redisService.deleteRefreshToken(user.getUserId());
      // 로그아웃 처리
      SecurityContextHolder.clearContext();

      return new ApiResponse<>(true, "회원 탈퇴가 성공적으로 처리되었습니다.");
    } catch (EntityNotFoundException e) {
      return new ApiResponse<>(false, e.getMessage());
    } catch (Exception e) {
      return new ApiResponse<>(false, "회원 탈퇴 처리 중 오류가 발생했습니다.");
    }
  }

  // 임시 비밀번호 생성 (6-20자리)
  public String createRandomPassword() {
    int length = RANDOM.nextInt(15) + 6; // 6-20자리
    return RANDOM
        .ints(length, 0, CHARS.length())
        .mapToObj(i -> String.valueOf(CHARS.charAt(i)))
        .collect(Collectors.joining());
  }

  // 공유자 검색
  public List<String> searchUsersByNickname(String keyword, String userId) {
    List<String> userNicknames = new ArrayList<>();
    for (User user : userRepository.findByNicknameContaining(keyword)) {
      if (user.getRole() == 2 || user.getUserId().equals(userId)) {
        continue;
      }
      userNicknames.add(user.getNickname());
    }
    return userNicknames;
  }

  public String getNicknameByUserId(String userId) {
    User user =
        userRepository.findNicknameByUserId(userId).orElseThrow(() -> new UserNotFoundException());
    return user.getNickname();
  }
}
