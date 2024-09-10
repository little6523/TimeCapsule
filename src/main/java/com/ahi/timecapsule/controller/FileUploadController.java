package com.ahi.timecapsule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
public class FileUploadController {

  // 외부 경로 설정
  private final String externalPath =
          Paths.get(System.getProperty("user.home"), "files").toString();

  // 이미지 업로드 시 상대 경로 변환 함수
  @PostMapping("/upload-image")
  public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
    try {
      // 저장 디렉토리 설정
//      String uploadDir = "src/main/resources/static/images/uploads/";
      String originalFileName = file.getOriginalFilename();
      String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

      String uuidFileName = UUID.randomUUID().toString() + extension;
      Path path = Paths.get(externalPath + uuidFileName);

      // 파일 저장
      Files.createDirectories(path.getParent());
      Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

      System.out.println("실제경로: " + path);

      // 상대 경로 설정
      String relativePath = "/images/uploads/" + uuidFileName;
      return ResponseEntity.ok(relativePath);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
    }
  }
}
