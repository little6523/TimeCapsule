package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.FindStoryResponseDTO;
import com.ahi.timecapsule.dto.UpdateStoryRequestDTO;
import com.ahi.timecapsule.exception.StoryNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.service.StoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/stories")
public class StoryController {
  private final StoryService storyService;

  public StoryController(StoryService storyService) {
    this.storyService = storyService;
  }

  // 로그인한 사용자의 ID 가져오기
  private String getCurrentUserId() {
//    return SecurityContextHolder.getContext().getAuthentication().getName();
    return "user123";
  }

  // 마이 스토리 목록 조회(전체/검색)
  @GetMapping
  public String listUserStories(@RequestParam(value = "keyword", required = false, defaultValue = "") String searchKeyword,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                HttpServletRequest request,
                                Model model) {
    String userId = getCurrentUserId();

    if (searchKeyword.isEmpty()) {
      setStoryPage(storyService.findUserStories(userId, page, size), model, "myStories", request);
    } else {
      setStoryPage(storyService.findMyStoriesByKeyword(userId, searchKeyword, page, size), model, "myStories", request);
    }
    return "storylist";
  }

  // 공유된 스토리 목록 조회(전체/검색)
  @GetMapping("/shared")
  public String listSharedStories(@RequestParam(value = "keyword", required = false, defaultValue = "") String searchKeyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  HttpServletRequest request,
                                  Model model) {
    String userId = getCurrentUserId();

    if (searchKeyword.isEmpty()) {
      setStoryPage(storyService.findSharedStories(userId, page, size), model, "sharedStories", request);
    } else {
      setStoryPage(storyService.findSharedStoriesByKeyword(userId, searchKeyword, page, size),
              model, "sharedStories", request);
    }
    return "storylist";
  }

  // 마이 스토리 상세 조회
  @GetMapping("/{id}")
  public String findStoryDetail(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {
    setStoryDetailPage(id, model, request);
    return "story-detail";
  }

  // 공유된 스토리 상세 조회
  @GetMapping("/shared/{id}")
  public String findSharedStoryDetail(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {
    setStoryDetailPage(id, model, request);
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
    try {
      storyService.updateStory(id, storyRequestDTO);
      return ResponseEntity.ok("스토리가 성공적으로 수정되었습니다.");
    } catch (StoryNotFoundException | UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
    } catch (StoryNotFoundException e) {
      response.put("success", false);
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception e) {
      response.put("success", false);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.ok(response);
  }

  // 스토리 목록 페이지 구현을 위한 스토리 데이터 설정
  private void setStoryPage(Page<FindStoryResponseDTO> storyPage,
                            Model model,
                            String tabName,
                            HttpServletRequest request) {

    model.addAttribute("storyPage", storyPage);
    model.addAttribute("tab", tabName);
    model.addAttribute("currentURI", request.getRequestURI());
  }

  // 상세 페이지 구현을 위한 스토리 데이터 설정
  private void setStoryDetailPage(Integer id, Model model, HttpServletRequest request) {
    FindStoryResponseDTO story = storyService.getStoryById(id);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
  }
}
