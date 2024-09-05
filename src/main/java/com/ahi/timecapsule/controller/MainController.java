package com.ahi.timecapsule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

	//root 페이지 경로
	@GetMapping("/")
	public String home(Model model) {
		// 테스트를 위해 userDTO를 null로 설정
		model.addAttribute("userDTO", null);

		return "main";
	}

	// 로그인한 상태를 시뮬레이션하는 추가 엔드포인트
	@GetMapping("/test-login")
	public String homeLoggedIn(Model model) {
		// 테스트를 위해 userDTO를 빈 객체로 설정
		model.addAttribute("userDTO", new Object());

		return "main";
	}
}
