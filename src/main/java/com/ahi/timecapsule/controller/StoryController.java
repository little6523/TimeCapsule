package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.FindStoryResponseDTO;
import com.ahi.timecapsule.dto.UpdateStoryRequestDTO;
import com.ahi.timecapsule.service.StoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  public String findStoryDetail(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {

    FindStoryResponseDTO story = storyService.getStoryById(id);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }

  // 공유된 스토리 상세 조회
  @GetMapping("/shared/{id}")
  public String findSharedStoryDetail(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {

    FindStoryResponseDTO story = storyService.getStoryById(id);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }

  // 수정 폼 조회
  @GetMapping("/{id}/edit")
  public String getUpdateStoryForm(@PathVariable("id") Integer id, Model model) {
    FindStoryResponseDTO story = storyService.getStoryById(id);
    model.addAttribute("story", storyService.getStoryById(id));
    return "story-update";
  }

  // 특정 스토리 수정
  @PutMapping("/{id}")
  public ResponseEntity<String> updateStory(@PathVariable("id") Integer id,
                                            @RequestBody UpdateStoryRequestDTO storyRequestDTO) {

    // 예외 세분화 수정 필요
    try {
      storyService.updateStory(id, storyRequestDTO);
      return ResponseEntity.ok("스토리가 성공적으로 수정되었습니다.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("스토리 수정 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
  }

  // 특정 스토리 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> deleteStory(@PathVariable("id") Integer id) {
    Map<String, Object> response = new HashMap<>();

    try {
      storyService.deleteStoryById(id);
      response.put("success", true);
//      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.ok(response);
  }
}
