package com.ahi.timecapsule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

  // root 페이지 경로
  @GetMapping("/")
  public String home() {
    return "index";
  }
}
