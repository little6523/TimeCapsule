package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.FindStoryResponseDTO;
import com.ahi.timecapsule.service.StoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/stories")
public class StoryController {
  private final StoryService storyService;

  public StoryController(StoryService storyService) {
    this.storyService = storyService;
  }

  // 마이 스토리 목록 조회
  @GetMapping
  public String listUserStories(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                HttpServletRequest request) {

    String userId = "user123";
    Page<FindStoryResponseDTO> storyPage = storyService.getUserStories(userId, page, size);
    model.addAttribute("storyPage", storyPage);
    model.addAttribute("tab", "myStories");
    model.addAttribute("currentURI", request.getRequestURI());
    return "storylist";
  }

  // 공유된 스토리 목록 조회
  @GetMapping("/shared")
  public String listSharedStories(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  HttpServletRequest request) {
    String userId = "user123";
    Page<FindStoryResponseDTO> storyPage = storyService.getSharedStoriesForUser(userId, page, size);
    model.addAttribute("storyPage", storyPage);
    model.addAttribute("tab", "sharedStories");
    model.addAttribute("currentURI", request.getRequestURI());
    return "storylist";
  }

  // 마이 스토리 상세 조회
  @GetMapping("/{id}")
  public String findStoryDetail(@PathVariable Integer id, Model model, HttpServletRequest request) {

    FindStoryResponseDTO story = storyService.getStoryById(id);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }

  // 공유된 스토리 상세 조회
  @GetMapping("/shared/{id}")
  public String findSharedStoryDetail(@PathVariable Integer id, Model model, HttpServletRequest request) {

    FindStoryResponseDTO story = storyService.getStoryById(id);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }
}
