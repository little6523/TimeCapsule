package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.request.StoryOptionDTO;
import com.ahi.timecapsule.service.ApiService;
import com.ahi.timecapsule.service.StoryService;
import java.io.IOException;
import java.util.List;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("/stories")
public class StoryController {

  private StoryService storyService;
  private ApiService apiService;

  private final String[] dialects = {"선택 안함", "강원도", "충청도", "경상도", "전라도", "제주도"};
  private final String[] speakers = {"선택 안함", "할머니", "할아버지", "어머니", "아버지", "손자", "손녀"};

  public StoryController(StoryService storyService, ApiService apiService) {
    this.storyService = storyService;
    this.apiService = apiService;
  }

  // 스토리 생성 폼 조회
  @GetMapping("/form")
  public String getStoryForm(Model model) {
//    model.addAttribute("StoryOptionDTO", new StoryOptionDTO());
    model.addAttribute("dialects", dialects);
    model.addAttribute("speakers", speakers);

    return "story-form";
  }

  // 음성 파일 및 사진 업로드
  @PostMapping("/form")
  public String uploadFile(
      @RequestPart List<MultipartFile> files, @ModelAttribute StoryOptionDTO storyOptionDTO)
      throws IOException, ParseException {

    for (MultipartFile file : files) {
      System.out.println(file.getOriginalFilename());
    }

    storyService.saveFiles(files);

    return "redirect:create";
  }

  @GetMapping("/create")
  public String stt() {
    return "test";
  }

  @ResponseBody
  @GetMapping(path = "/story", produces = "text/event-stream")
  public ResponseEntity<SseEmitter> createStory() throws IOException, ParseException, InterruptedException {
    apiService.post();

    List<String> contents = apiService.get();

    System.out.println(contents.get(0));

    return ResponseEntity.ok(apiService.createContent(contents));
  }

  @ResponseBody
  @GetMapping("/stt/auth")
  public ResponseEntity<String> test() throws Exception {
    apiService.auth();

    return ResponseEntity.ok("확인되었어요!");
  }
}
