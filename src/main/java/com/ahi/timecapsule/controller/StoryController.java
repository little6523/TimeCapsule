package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.FindImageResponseDTO;
import com.ahi.timecapsule.dto.FindStoryResponseDTO;
import com.ahi.timecapsule.dto.UpdateStoryRequestDTO;
import com.ahi.timecapsule.exception.StoryNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.service.ImageService;
import com.ahi.timecapsule.service.StoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/stories")
public class StoryController {
  private final StoryService storyService;
  private final ImageService imageService;

  public StoryController(StoryService storyService, ImageService imageService) {
    this.storyService = storyService;
    this.imageService = imageService;
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
    Page<FindStoryResponseDTO> storyPage;

    if (searchKeyword.isEmpty()) {
      storyPage = storyService.findUserStories(userId, page, size);
    } else {
      storyPage = storyService.findMyStoriesByKeyword(userId, searchKeyword, page, size);
    }

    model.addAttribute("storyPage", storyPage);
    model.addAttribute("tab", "myStories");
    model.addAttribute("currentURI", request.getRequestURI());
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
    Page<FindStoryResponseDTO> storyPage;

    if (searchKeyword.isEmpty()) {
      storyPage = storyService.findSharedStories(userId, page, size);
    } else {
      storyPage = storyService.findSharedStoriesByKeyword(userId, searchKeyword, page, size);
    }

    model.addAttribute("storyPage", storyPage);
    model.addAttribute("tab", "sharedStories");
    model.addAttribute("currentURI", request.getRequestURI());
    return "storylist";
  }

  // 마이 스토리 상세 조회
  @GetMapping("/{id}")
  public String findStoryDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    FindStoryResponseDTO story = storyService.getStoryById(id);

    List<String> encodedImages = story.getImages().stream()
            .map(image -> {
              try {
                String encodedImage = imageService.encodeImageToBase64(image.getUrl());
                return encodedImage;
              } catch (IOException e) {
                e.printStackTrace();
                return "";
              }
            }).toList();

//    List<String> encodedImages = story.getImages().stream()
//            .map(image -> {
//              try {
//                byte[] imageBytes = Files.readAllBytes(Paths.get(image.getUrl()));
//                return Base64.getEncoder().encodeToString(imageBytes);
//              } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//              }
//            })
//            .filter(Objects::nonNull)
//            .toList();

    model.addAttribute("images", encodedImages);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }

  // 공유된 스토리 상세 조회
  @GetMapping("/shared/{id}")
  public String findSharedStoryDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    FindStoryResponseDTO story = storyService.getStoryById(id);

//    List<String> encodedImages = story.getImages().stream()
//            .map(image -> {
//              try {
//                byte[] imageBytes = Files.readAllBytes(Paths.get(image.getUrl()));
//                return Base64.getEncoder().encodeToString(imageBytes);
//              } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//              }
//            })
//            .filter(Objects::nonNull)
//            .toList();

    List<String> encodedImages = story.getImages().stream()
            .map(image -> {
              try {
                String encodedImage = imageService.encodeImageToBase64(image.getUrl());
                return encodedImage;
              } catch (IOException e) {
                e.printStackTrace();
                return "";
              }
            }).toList();

    model.addAttribute("images", encodedImages);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }

  // 수정 폼 조회
  @GetMapping("/{id}/edit")
  public String getUpdateStoryForm(@PathVariable("id") Long id, Model model) {
    FindStoryResponseDTO story = storyService.getStoryById(id);
    Map<Long, String> encodedImages = new HashMap<>();
    String imageUrl;
    for (FindImageResponseDTO image : story.getImages()) {
      try {
        byte[] imageBytes = Files.readAllBytes(Paths.get(image.getUrl()));
        imageUrl = Base64.getEncoder().encodeToString(imageBytes);
      } catch (IOException e) {
        imageUrl = null;
      }
      encodedImages.put(image.getId(), imageUrl);
    };
//  }
//
//  Map<String, Long> encodedImages = story.getImages().stream()
//          .map(image -> {
//            try {
//              byte[] imageBytes = Files.readAllBytes(Paths.get(image.getUrl()));
//              return Base64.getEncoder().encodeToString(imageBytes);
//            } catch (IOException e) {
//              e.printStackTrace();
//              return null;
//            }
//          })
//          .filter(Objects::nonNull)
//          .toList();

    model.addAttribute("images",encodedImages);
    model.addAttribute("story",story);
    return"story-update";
}

// 특정 스토리 수정
@PutMapping("/{id}")
public ResponseEntity<String> updateStory(@PathVariable("id") Long id,
                                          @RequestParam("storyId") Long storyId,
                                          @RequestParam("title") String title,
                                          @RequestParam("content") String content,
                                          @RequestParam("isShared") boolean isShared,
                                          @RequestParam("sharedWithUsers") List<String> sharedWithUsers,
                                          @RequestParam("deletedImages") List<Long> deletedImages,
                                          @RequestParam("images") List<MultipartFile> images) {

  List<String> imageUrls = new ArrayList<>();
  try {
    if (!images.isEmpty() && !images.get(0).isEmpty()) {
      List<String> newImageUrls = storyService.saveFiles(images);
      imageUrls.addAll(newImageUrls);
    }
  } catch (Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
  }

  imageService.updateImages(deletedImages, imageUrls, storyId);

  try {
    UpdateStoryRequestDTO storyRequestDTO = UpdateStoryRequestDTO.builder()
            .title(title)
            .content(content)
            .sharedWithUsers(sharedWithUsers)
            .isShared(isShared)
            .build();

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
public ResponseEntity<Map<String, Object>> deleteStory(@PathVariable("id") Long id) {
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
}
