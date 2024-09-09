package com.ahi.timecapsule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ahi.timecapsule.config.JwtTokenProvider;
import com.ahi.timecapsule.config.PasswordEncoderConfig;
import com.ahi.timecapsule.config.RedisService;
import com.ahi.timecapsule.dto.*;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.oauth.CustomOAuth2User;
import com.ahi.timecapsule.oauth.GoogleOAuth2UserInfo;
import com.ahi.timecapsule.oauth.KakaoOAuth2UserInfo;
import com.ahi.timecapsule.oauth.OAuth2UserInfo;
import com.ahi.timecapsule.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks private UserService userService;

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoderConfig passwordEncoderConfig;

  @Mock private JwtTokenProvider jwtTokenProvider;

  @Mock private OAuth2UserInfo oAuth2UserInfo;

  @Mock private RedisService redisService;

  @Mock private EmailService emailService;

  @Mock private PasswordEncoder passwordEncoder;

  // OAuth2.0 구글 로그인 성공 케이스
  @Test
  void oauthLogin_Success_Google() {
    String oneTimeCode = "validGoogleOneTimeCode";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("sub", "123456789");
    attributes.put("name", "Test Google User");
    attributes.put("email", "testgoogle@test.com");

    oAuth2UserInfo = new GoogleOAuth2UserInfo(attributes);

    CustomOAuth2User oAuth2User = new CustomOAuth2User(oAuth2UserInfo);

    when(redisService.getUserAndDeleteOneTimeCode(oneTimeCode)).thenReturn(oAuth2User);
    when(jwtTokenProvider.generateOAuth2AccessToken(oAuth2User)).thenReturn("accessToken");
    when(jwtTokenProvider.generateOAuth2RefreshToken(oAuth2User)).thenReturn("refreshToken");

    TokenResponse response = userService.oauthLogin(oneTimeCode);

    assertNotNull(response);
    assertEquals("accessToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());
    verify(redisService).saveRefreshToken("123456789", "refreshToken");
  }

  // OAuth2.0 카카오 로그인 성공 케이스
  @Test
  void oauthLogin_Success_Kakao() {
    String oneTimeCode = "validKakaoOneTimeCode";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", 987654321L);
    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", "Test Kakao User");
    attributes.put("properties", properties);
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "testkakao@test.com");
    attributes.put("kakao_account", kakaoAccount);

    oAuth2UserInfo = new KakaoOAuth2UserInfo(attributes);

    CustomOAuth2User oAuth2User = new CustomOAuth2User(oAuth2UserInfo);

    when(redisService.getUserAndDeleteOneTimeCode(oneTimeCode)).thenReturn(oAuth2User);
    when(jwtTokenProvider.generateOAuth2AccessToken(oAuth2User)).thenReturn("accessToken");
    when(jwtTokenProvider.generateOAuth2RefreshToken(oAuth2User)).thenReturn("refreshToken");

    TokenResponse response = userService.oauthLogin(oneTimeCode);

    assertNotNull(response);
    assertEquals("accessToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());

    verify(redisService).saveRefreshToken("987654321", "refreshToken");
  }

  // OAuth2.0 로그인 실패 케이스
  @Test
  void oauthLogin_Failure_InvalidOneTimeCode() {
    String oneTimeCode = "invalidOneTimeCode";
    when(redisService.getUserAndDeleteOneTimeCode(oneTimeCode)).thenReturn(null);

    TokenResponse response = userService.oauthLogin(oneTimeCode);

    assertNull(response);

    verify(jwtTokenProvider, never()).generateOAuth2AccessToken(any());
    verify(jwtTokenProvider, never()).generateOAuth2RefreshToken(any());
    verify(redisService, never()).saveRefreshToken(anyString(), anyString());
  }

  // 회원 정보 조회 성공 케이스
  @Test
  void getUserInfo_Success() {
    User user =
        User.builder()
            .userId("testID")
            .email("test@test.com")
            .nickname("testNickname")
            .password("testEncodePassword")
            .role(1)
            .build();

    when(userRepository.findById("testID")).thenReturn(Optional.of(user));

    UserDTO result = userService.getUserInfo("testID");

    assertNotNull(result);
    assertEquals("testID", result.getUserId());
  }

  // 회원 정보 조회 실패 케이스
  @Test
  void getUserInfo_Failure() {
    String userId = "notFoundUser";

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getUserInfo(userId));
  }

  // 임시 비밀번호 발급 성공 케이스
  @Test
  void issueTemporaryPassword_Success() {
    TemporaryPasswordDTO dto = new TemporaryPasswordDTO("testUser", "test@test.com");
    User user = User.builder().userId("testUser").email("test@test.com").build();

    when(userRepository.findByUserIdAndEmail(dto.getId(), dto.getEmail()))
        .thenReturn(Optional.of(user));
    when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

    ApiResponse<Void> response = userService.issueTemporaryPassword(dto);

    assertTrue(response.isSuccess());
    assertEquals("임시 비밀번호가 이메일로 전송되었습니다.", response.getMessage());
    verify(emailService).sendTempPasswordEmail(eq("test@test.com"), anyString());
  }

  // 임시 비밀번호 발급 실패 케이스(UserNotFound)
  @Test
  void issueTemporaryPassword_UserNotFound() {
    TemporaryPasswordDTO dto = new TemporaryPasswordDTO("notFoundUser", "test@test.com");
    when(userRepository.findByUserIdAndEmail(dto.getId(), dto.getEmail()))
        .thenReturn(Optional.empty());

    ApiResponse<Void> response = userService.issueTemporaryPassword(dto);

    assertFalse(response.isSuccess());
    assertEquals("일치 하는 계정 정보가 없습니다.", response.getMessage());
  }

  // 임시 비밀번호 발급 실패 케이스(MailException)
  @Test
  void issueTemporaryPassword_MailException() {
    TemporaryPasswordDTO dto = new TemporaryPasswordDTO("testUser", "test@test.com");
    User user = User.builder().userId("testUSer").email("test@test.com").build();

    when(userRepository.findByUserIdAndEmail(dto.getId(), dto.getEmail()))
        .thenReturn(Optional.of(user));
    when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    doThrow(new MailException("Mail sending failed") {})
        .when(emailService)
        .sendTempPasswordEmail(anyString(), anyString());

    ApiResponse<Void> response = userService.issueTemporaryPassword(dto);

    assertFalse(response.isSuccess());
    assertEquals("임시 비밀번호 이메일 전송에 실패했습니다.", response.getMessage());
  }

  // 회원 정보 수정 성공 케이스
  @Test
  void updateUser_Success() {
    UserUpdateDTO dto =
        new UserUpdateDTO(
            "testUser", "oldPassword", "newPassword", "newEmail@test.com", "newNickname");
    User user =
        User.builder()
            .userId("testUser")
            .password("oldPassword")
            .email("newEmail@test.com")
            .nickname("newNickname")
            .build();

    when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
    when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
    when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
    when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("newPassword");

    ApiResponse<String> response = userService.updateUser(dto);

    assertTrue(response.isSuccess());
    assertEquals("사용자 정보가 성공적으로 업데이트되었습니다.", response.getMessage());
    assertEquals("testUser", response.getData());
  }

  // 회원 정보 수정 실패 케이스(UserNotFound)
  @Test
  void updateUser_UserNotFound() {
    UserUpdateDTO dto =
        new UserUpdateDTO(
            "notFoundUser", "oldPassword", "newPassword", "newEmail@test.com", "newNickname");
    when(userRepository.findById(dto.getId())).thenReturn(Optional.empty());

    ApiResponse<String> response = userService.updateUser(dto);

    assertFalse(response.isSuccess());
    assertEquals("요청하신 사용자 정보를 찾을 수 없습니다.", response.getMessage());
  }

  // 회원 정보 수정 실패 케이스(IncorrectPassword)
  @Test
  void updateUser_IncorrectPassword() {
    UserUpdateDTO dto =
        new UserUpdateDTO(
            "testUser", "wrongPassword", "newPassword", "newEmail@test.com", "newNickname");
    User user = User.builder().userId("testUser").password("oldPassword").build();

    when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
    when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
    when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

    ApiResponse<String> response = userService.updateUser(dto);

    assertFalse(response.isSuccess());
    assertEquals("기존 비밀번호가 일치하지 않습니다.", response.getMessage());
  }

  // 닉네임 중복인 경우 케이스
  @Test
  void isNicknameDuplicate_True() {
    String nickname = "existingNickname";
    when(userRepository.existsByNickname(nickname)).thenReturn(true);

    boolean result = userService.isNicknameDuplicate(nickname);

    assertTrue(result);
  }

  // 닉네임 중복이 아닐 경우 케이스
  @Test
  void isNicknameDuplicate_False() {
    String nickname = "newNickname";
    when(userRepository.existsByNickname(nickname)).thenReturn(false);

    boolean result = userService.isNicknameDuplicate(nickname);

    assertFalse(result);
  }

  // 이메일 중복인 경우 케이스
  @Test
  void isEmailDuplicate_True() {
    String email = "existing@test.com";
    when(userRepository.existsByEmail(email)).thenReturn(true);

    boolean result = userService.isEmailDuplicate(email);

    assertTrue(result);
  }

  // 이메일 중복이 아닐 경우 케이스
  @Test
  void isEmailDuplicate_False() {
    String email = "new@test.com";
    when(userRepository.existsByEmail(email)).thenReturn(false);

    boolean result = userService.isEmailDuplicate(email);

    assertFalse(result);
  }

  // 회원 탈퇴 성공 케이스
  @Test
  void deleteUser_Success() {
    UserWithdrawalDTO dto = new UserWithdrawalDTO("testUser", "password");
    User user = User.builder().userId("testUser").password("password").build();

    when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
    when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
    when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

    ApiResponse<Void> response = userService.deleteUser(dto);

    assertTrue(response.isSuccess());
    assertEquals("회원 탈퇴가 성공적으로 처리되었습니다.", response.getMessage());
    verify(userRepository).delete(user);
    verify(redisService).deleteRefreshToken("testUser");
  }

  // 회원 탈퇴 실패 케이스(UserNotFound)
  @Test
  void deleteUser_UserNotFound() {
    UserWithdrawalDTO dto = new UserWithdrawalDTO("notFoundUser", "password");
    when(userRepository.findById(dto.getId())).thenReturn(Optional.empty());

    ApiResponse<Void> response = userService.deleteUser(dto);

    assertFalse(response.isSuccess());
    assertEquals("요청하신 사용자 정보를 찾을 수 없습니다.", response.getMessage());
  }

  // 회원 탈퇴 실패 케이스(IncorrectPassword)
  @Test
  void deleteUser_IncorrectPassword() {
    UserWithdrawalDTO dto = new UserWithdrawalDTO("testUser", "password");
    User user = User.builder().userId("testUser").password("encodedPassword").build();

    when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
    when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
    when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

    ApiResponse<Void> response = userService.deleteUser(dto);

    assertFalse(response.isSuccess());
    assertEquals("기존 비밀번호가 일치하지 않습니다.", response.getMessage());
  }

  // 임시비밀번호 생성 케이스
  @Test
  void createRandomPassword_LengthInRange() {
    String password = userService.createRandomPassword();

    assertNotNull(password);
    assertTrue(password.length() >= 6 && password.length() <= 20);
    assertTrue(password.matches(".*[A-Z].*"));
    assertTrue(password.matches(".*[a-z].*"));
    assertTrue(password.matches(".*\\d.*"));
  }
}
