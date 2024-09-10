package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.UpdateStoryRequestDTO;
import com.ahi.timecapsule.dto.request.StoryContentDTO;
import com.ahi.timecapsule.dto.request.StoryOptionDTO;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.exception.StoryNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.service.ImageService;
import com.ahi.timecapsule.service.StoryService;
import com.ahi.timecapsule.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/stories")
public class StoryController {

  private final UserService userService;
  private final StoryService storyService;
  private final ImageService imageService;

  private final String[] dialects = {"선택 안함", "강원도", "충청도", "경상도", "전라도", "제주도"};
  private final String[] speakers = {"선택 안함", "할머니", "할아버지", "어머니", "아버지", "손자", "손녀"};

  public StoryController(
      UserService userService, StoryService storyService, ImageService imageService) {
    this.userService = userService;
    this.storyService = storyService;
    this.imageService = imageService;
  }

  // 스토리 생성 폼 조회
  @GetMapping("/form")
  public String getStoryForm(Model model) {
    model.addAttribute("dialects", dialects);
    model.addAttribute("speakers", speakers);

    return "story-form";
  }

  // 공유된 스토리 상세 조회
  @GetMapping("/stories/shared/{id}")
  public String findSharedStoryDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
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

    model.addAttribute("images", encodedImages);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }

  // 수정 폼 조회
  @GetMapping("/stories/{id}/edit")
  public String getUpdateStoryForm(@PathVariable("id") Long id, Model model) {
    FindStoryResponseDTO story = storyService.getStoryById(id);
    Map<Long, String> encodedImages = new HashMap<>();
    String imageUrl;
    for (ImageDTO image : story.getImages()) {
      try {
        byte[] imageBytes = Files.readAllBytes(Paths.get(image.getUrl()));
        imageUrl = Base64.getEncoder().encodeToString(imageBytes);
      } catch (IOException e) {
        imageUrl = null;
      }
      encodedImages.put(image.getId(), imageUrl);
    };

    model.addAttribute("images",encodedImages);
    model.addAttribute("story",story);
    return"story-update";
  }

  // 음성 파일 업로드 및 화자, 사투리 설정
  @PostMapping("/form")
  public String uploadFile(
      @RequestPart MultipartFile file,
      @ModelAttribute StoryOptionDTO storyOptionDTO,
      HttpSession session)
      throws IOException {

    System.out.println(file.getOriginalFilename());

    List<MultipartFile> files = new ArrayList<>();
    files.add(file);

    List<String> filePath = storyService.saveFiles(files);
    storyOptionDTO.setSoundFile(filePath.get(0));

    session.setAttribute("StoryOptionDTO", storyOptionDTO);

    return "redirect:complete-form";
  }

  // 특정 스토리 수정
  @PutMapping("/stories/{id}")
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
  @DeleteMapping("/stories/{id}")
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

  // 커뮤니티 스토리 목록 조회(전체/검색)
  @GetMapping("/community/stories")
  public String getCommunityStoryList(@RequestParam(value = "keyword", required = false, defaultValue = "") String searchKeyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      HttpServletRequest request,
                                      Model model) {
    Page<FindStoryResponseDTO> storyPage;

    if (searchKeyword.isEmpty()) {
      storyPage = storyService.findCommunityStories(page, size);
    } else {
      storyPage = storyService.findCommunityStoriesByKeyword(searchKeyword, page, size);
    }

    model.addAttribute("storyPage", storyPage);
    model.addAttribute("tab", "community");
    model.addAttribute("currentURI", request.getRequestURI());
    return "storylist";
  }

  // 커뮤니티 스토리 상세 조회
  @GetMapping("/community/stories/{id}")
  public String findCommunityStoryDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
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

    model.addAttribute("images", encodedImages);
    model.addAttribute("story", story);
    model.addAttribute("currentURI", request.getRequestURI());
    return "story-detail";
  }

  @GetMapping("/complete-form")
  public String getCreateStoryForm(
      @ModelAttribute("StoryOptionDTO") StoryOptionDTO storyOptionDTO) {

    return "story-created";
  }

  @GetMapping("/search")
  @ResponseBody
  public List<User> searchUsers(@RequestParam("keyword") String keyword) {

    return userService.searchUsersByNickname(keyword);
  }

  @PostMapping
  public String createStory(
      @RequestPart(value = "images", required = false) List<MultipartFile> files,
      @ModelAttribute StoryContentDTO storyContentDTO,
      @ModelAttribute("userId") String userId,
      HttpSession session)
      throws IOException {

    List<String> filesPath = new ArrayList<>();
    if (files != null && !files.isEmpty()) {
      filesPath = storyService.saveFiles(files);
    }

    System.out.println("id = " + userId);

    StoryOptionDTO storyOptionDTO = (StoryOptionDTO) session.getAttribute("StoryOptionDTO");

    storyService.save(storyOptionDTO, storyContentDTO, filesPath, userId);

    //    return "redirect:story-created/" + storyDTO.getUser();
    return "redirect:stories/complete-form";
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateStory(
      @PathVariable("id") Long id,
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
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("이미지 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
    }

    imageService.updateImages(deletedImages, imageUrls, storyId);

    try {
      UpdateStoryRequestDTO storyRequestDTO =
          UpdateStoryRequestDTO.builder()
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
}
