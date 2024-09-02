package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.UserDTO;
import com.ahi.timecapsule.dto.UserLoginDTO;
import com.ahi.timecapsule.dto.UserSignUpDTO;
import com.ahi.timecapsule.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping
public class UserController {
  private final UserService userService;

  @GetMapping("/signUpPage")
  public String signUpPage(Model model) {
    UserDTO userSignUp = UserDTO.builder().build();
    model.addAttribute("userSignUp", userSignUp);
    return "signup";
  }

  @PostMapping("/signUp")
  public String signUp(@Valid @ModelAttribute("userSignUp") UserSignUpDTO userSignUp, BindingResult bindingResult, Model model) {

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
    } catch (DataIntegrityViolationException e) {  // 데이터베이스 제약 조건 위반 예외 처리 (아이디, 이메일, 닉네임 중복)
      model.addAttribute("errorMessage", e.getMessage());
      return "signup";  // 예외 발생 시 회원가입 폼으로 다시 이동
    } catch (Exception e) {  // 그 외의 일반적인 예외 처리
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

  @PostMapping("/login")
  public String login(@ModelAttribute("userLogin") UserLoginDTO userLogin, HttpServletResponse response, Model model) {
    System.out.println("시작!");
    System.out.println(userLogin.getUserId());
    String token = userService.login(userLogin);
    System.out.println("토큰생성");

    if (token != null) {
      Cookie jwtCookie = new Cookie("jwt", token);
      jwtCookie.setPath("/");
      jwtCookie.setMaxAge(3600);
      jwtCookie.setHttpOnly(true); // 자바스크립트에서 접근 불가하게 설정
//            jwtCookie.setSecure(true);
      response.addCookie(jwtCookie);
      System.out.println("꺄악");

      return "redirect:/main";
    } else {
      model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
      return "login";
    }
  }

  // 메인 페이지로 이동
  @GetMapping("/main")
  public String mainPage() {
    return "main";
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
  public ResponseEntity<Boolean> isPasswordMatching(@RequestBody UserSignUpDTO userSignUp){
    boolean isMatch = userService.isPasswordMatching(userSignUp);
    return ResponseEntity.ok(isMatch);
  }

}
