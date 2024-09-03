package com.ahi.timecapsule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stories")
public class StoryController {

  @GetMapping("/create")
  public String stt() {
    return "test";
  }
}
