package com.ahi.timecapsule.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

  // 이미지 업로드 시 상대 경로 변환 함수
  @PostMapping("/upload-image")
  public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
    try {
      // 저장 디렉토리 설정
      String uploadDir = "src/main/resources/static/images/uploads/";
      String fileName = file.getOriginalFilename();
      Path path = Paths.get(uploadDir + fileName);

      // 파일 저장
      Files.createDirectories(path.getParent());
      Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

      // 상대 경로 설정
      String relativePath = "/images/uploads/" + fileName;
      return ResponseEntity.ok(relativePath);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
    }
  }
}
