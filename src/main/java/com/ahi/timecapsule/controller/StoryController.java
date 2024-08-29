package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.StoryDTO;
import com.ahi.timecapsule.dto.StoryOptionDTO;
import com.ahi.timecapsule.service.StoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/stories")
public class StoryController {

    private StoryService storyService;

    private final String[] dialects = {"선택 안함", "강원도", "충청도", "경상도", "전라도", "제주도"};
    private final String[] speakers = {"선택 안함", "할머니", "할아버지", "어머니", "아버지", "손자", "손녀"};

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    // 스토리 생성 폼 조회
    @GetMapping("/form")
    public String getStoryForm(Model model) {
        model.addAttribute("StoryOptionDTO", new StoryOptionDTO());
        model.addAttribute("dialects", dialects);
        model.addAttribute("speakers", speakers);

        return "story-form";
    }

    // 음성 파일 및 사진 업로드
    @PostMapping("/form")
    public String uploadFile(
            @RequestPart List<MultipartFile> files,
            @ModelAttribute StoryOptionDTO storyOptionDTO) throws IOException {

        for (MultipartFile file : files) {
            System.out.println(file.getOriginalFilename());
        }

        storyService.saveFiles(files);

        return "redirect:form";
    }
}
