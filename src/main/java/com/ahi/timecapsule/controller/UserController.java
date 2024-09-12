package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.config.JwtTokenProvider;
import com.ahi.timecapsule.config.RedisService;
import com.ahi.timecapsule.dto.*;
import com.ahi.timecapsule.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping
public class UserController {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;

  @GetMapping("/signUpPage")
  public String signUpPage(Model model) {
    UserDTO userSignUp = UserDTO.builder().build();
    model.addAttribute("userSignUp", userSignUp);
    return "signup";
  }

  @PostMapping("/signUp")
  public String signUp(
      @Valid @ModelAttribute("userSignUp") UserSignUpDTO userSignUp,
      BindingResult bindingResult,
      Model model) {

    // 비밀번호 일치 여부 확인
    if (!userService.isPasswordMatching(userSignUp)) {
      bindingResult.rejectValue("confirmPassword", "error.userDto", "비밀번호가 일치하지 않습니다.");
    }
    // 백엔드 단에서도 유효성 검증
    if (bindingResult.hasErrors()) {
      model.addAttribute("errorMessage", "입력값에 오류가 있습니다. 다시 확인해주세요.");
      return "signup";
    }

    try {
      userService.registerUser(userSignUp);
    } catch (DataIntegrityViolationException e) { // 데이터베이스 제약 조건 위반 예외 처리 (아이디, 이메일, 닉네임 중복)
      model.addAttribute("errorMessage", e.getMessage());
      return "signup"; // 예외 발생 시 회원가입 폼으로 다시 이동
    } catch (Exception e) { // 그 외의 일반적인 예외 처리
      model.addAttribute("errorMessage", "알 수 없는 오류가 발생했습니다.");
      return "signup";
    }

    return "redirect:/login";
  }

  @GetMapping("/login")
  public String login(Model model) {
    UserDTO userLogin = UserDTO.builder().build();
    model.addAttribute("userLogin", userLogin);
    return "login";
  }

  // 로컬 스토리지 방식
  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<?> login(
      @RequestBody UserLoginDTO userLogin, HttpServletResponse response, Model model) {

    // 로그인 로직 수행
    String token = userService.login(userLogin);

    // 로그인 성공 시 토큰을 응답 헤더에 추가
    response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return ResponseEntity.ok().build();
  }

  // OAuth2.0 로그인
  @PostMapping("/oauth/login")
  public ResponseEntity<?> oauthLogin(@RequestBody TokenExchangeRequest request) {
    TokenResponse tokenResponse = userService.oauthLogin(request.getOneTimeCode());

    if (tokenResponse != null) {
      return ResponseEntity.ok(tokenResponse);
    } else {
      return ResponseEntity.badRequest().body("유효하지 않거나 만료된 일회성 코드입니다.");
    }
  }

  // 메인 페이지 이동
  @GetMapping("/main")
  public String main(Model model) {
    return "main";
  }

  // 페이지 접속 시, 토큰 유효성 검사
  @GetMapping("/valid-token")
  public ResponseEntity<?> validateToken(
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
      HttpServletResponse response) {
    // Access Token 추출
    String accessToken =
        authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
            ? authorizationHeader.substring(7)
            : null;

    if (accessToken == null) {
      throw new RuntimeException("Access Token이 제공되지 않았습니다.");
    }

    // Access Token에서 사용자 이름 추출
    String username = jwtTokenProvider.getUsernameFromJwtToken(accessToken);

    // Redis에서 Refresh Token 가져오기
    String refreshToken = redisService.getRefreshToken(username);

    // Access Token 검증 및 필요한 경우 재발급
    String validAccessToken = userService.validateAndRefreshAccessToken(accessToken, refreshToken);
    String role = jwtTokenProvider.getAuthoritiesFromJwtToken(validAccessToken);

    if (role.contains("ROLE_USER") || role.contains("ROLE_ADMIN")) {
      // 유효한 Access Token으로 사용자 정보 추출
      response.addHeader("X-User-Id", username); // 사용자 정보 추가
      response.addHeader("X-User-Role", role);
      response.addHeader(
          HttpHeaders.AUTHORIZATION, "Bearer " + validAccessToken); // 새로운 Access Token 반환
      return ResponseEntity.ok().build();
    } else {
      throw new RuntimeException("권한이 없습니다.");
    }
  }

  // 페이지 접속 시, 토큰 유효성 검사
  @GetMapping("/valid-token-admin")
  public ResponseEntity<?> validateTokenAdmin(
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
      HttpServletResponse response) {
    // Access Token 추출
    String accessToken =
        authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
            ? authorizationHeader.substring(7)
            : null;

    if (accessToken == null) {
      throw new RuntimeException("Access Token이 제공되지 않았습니다.");
    }

    // Access Token에서 사용자 이름 추출
    String username = jwtTokenProvider.getUsernameFromJwtToken(accessToken);

    // Redis에서 Refresh Token 가져오기
    String refreshToken = redisService.getRefreshToken(username);

    // Access Token 검증 및 필요한 경우 재발급
    String validAccessToken = userService.validateAndRefreshAccessToken(accessToken, refreshToken);
    String role = jwtTokenProvider.getAuthoritiesFromJwtToken(validAccessToken);

    if (role.contains("ROLE_ADMIN")) {
      // 유효한 Access Token으로 사용자 정보 추출
      response.addHeader("X-User-Id", username); // 사용자 정보 추가
      response.addHeader(
          HttpHeaders.AUTHORIZATION, "Bearer " + validAccessToken); // 새로운 Access Token 반환

      return ResponseEntity.ok().build();
    } else {
      throw new RuntimeException("권한이 없습니다.");
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      // 토큰이 제공되지 않았거나 잘못된 형식일 때 예외를 던짐
      throw new RuntimeException("Access Token이 제공되지 않았습니다.");
    }

    String token = authorizationHeader.substring(7); // "Bearer " 부분 제거

    // 토큰이 유효한지 검증
    if (!jwtTokenProvider.validateToken(token)) {
      // 토큰이 유효하지 않을 때 예외를 던짐
      throw new RuntimeException("유효하지 않은 토큰");
    }

    // 토큰을 블랙리스트에 추가하여 무효화
    redisService.addToBlacklist(token);
    return ResponseEntity.ok("로그아웃 성공");
  }

  // 아이디, 이메일, 닉네임 중복 확인
  @PostMapping("/api/users/check-duplicate")
  public ResponseEntity<Boolean> checkDuplicate(@RequestBody Map<String, String> request) {
    String field = request.get("field");
    String value = request.get("value");
    boolean isDuplicate = userService.isDuplicate(field, value);
    return ResponseEntity.ok(isDuplicate);
  }

  // 비밀번호 매칭 확인
  @PostMapping("/api/users/password-match")
  public ResponseEntity<Boolean> isPasswordMatching(@RequestBody UserSignUpDTO userSignUp) {
    boolean isMatch = userService.isPasswordMatching(userSignUp);
    return ResponseEntity.ok(isMatch);
  }

  // 페이지 이동(비밀번호 찾기)
  @GetMapping("/users/password/find")
  public String findPasswordPage() {
    return "account/reset-password";
  }

  // 임시 비밀번호 발급
  @PostMapping("/users/password/temporary")
  public ResponseEntity<ApiResponse<Void>> issueTemporaryPassword(
      @Valid @RequestBody TemporaryPasswordDTO temporaryPasswordDTO, BindingResult bindingResult) {
    // 유효성 검사(첫번쨰 오류 메시지만 담아서 사용자에게 반환)
    if (bindingResult.hasErrors()) {
      FieldError fieldError = bindingResult.getFieldError();
      if (fieldError != null) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>(false, fieldError.getDefaultMessage()));
      }
    }

    ApiResponse<Void> response = userService.issueTemporaryPassword(temporaryPasswordDTO);
    if (!response.isSuccess()) {
      return ResponseEntity.badRequest().body(response);
    }
    return ResponseEntity.ok(response);
  }

  // 계정 관리 페이지 이동
  @GetMapping("/users/info")
  public String getUserInfo(Model model, @RequestParam String userId) {
    UserDTO userDTO = userService.getUserInfo(userId);
    model.addAttribute("user", userDTO);
    return "account/account-management";
  }

  // 회원 수정
  @PatchMapping("/users/update")
  public ResponseEntity<ApiResponse<String>> updateUser(
      @Valid @RequestBody UserUpdateDTO userUpdateDTO, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      FieldError fieldError = bindingResult.getFieldError();
      if (fieldError != null) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>(false, fieldError.getDefaultMessage()));
      }
    }

    ApiResponse<String> response = userService.updateUser(userUpdateDTO);
    if (!response.isSuccess()) {
      return ResponseEntity.badRequest().body(response);
    }

    return ResponseEntity.ok(response);
  }

  // 닉네임 중복체크
  @GetMapping("/users/checkDuplicateNickname")
  public ResponseEntity<Boolean> checkDuplicateNickname(@RequestParam String nickname) {
    boolean exists = userService.isNicknameDuplicate(nickname);
    return ResponseEntity.ok(exists);
  }

  // 이메일 중복체크
  @GetMapping("/users/checkDuplicateEmail")
  public ResponseEntity<Boolean> checkDuplicateEmail(@RequestParam String email) {
    boolean exists = userService.isEmailDuplicate(email);
    return ResponseEntity.ok(exists);
  }

  // 회원 탈퇴
  @DeleteMapping("/users/delete")
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @Valid @RequestBody UserWithdrawalDTO userWithdrawalDTO, BindingResult bindingResult) {
    // 유효성 검사
    if (bindingResult.hasErrors()) {
      FieldError fieldError = bindingResult.getFieldError();
      if (fieldError != null) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>(false, fieldError.getDefaultMessage()));
      }
    }

    ApiResponse<Void> response = userService.deleteUser(userWithdrawalDTO);
    if (!response.isSuccess()) {
      return ResponseEntity.badRequest().body(response);
    }
    return ResponseEntity.ok(response);
  }
}
