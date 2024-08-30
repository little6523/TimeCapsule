package com.ahi.timecapsule.service;

import com.ahi.timecapsule.repository.ImageRepository;
import com.ahi.timecapsule.repository.StoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class StoryService {

  private StoryRepository storyRepository;

  private ImageRepository imageRepository;

  private final List<String> soundFileExtensions =
      Arrays.asList("mp3", "wav", "ogg", "flac", "aac", "m4a", "wma");

  private final List<String> imageFileExtensions =
      Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff");

  // 업로드 파일 저장 경로를 외부로 설정
  private final String externalPath =
      Paths.get(System.getProperty("user.home"), "files").toString();

  public StoryService(StoryRepository storyRepository, ImageRepository imageRepository) {
    this.storyRepository = storyRepository;
    this.imageRepository = imageRepository;
  }

  // 스토리 생성 시 필요한 인터뷰(사운드), 사진(이미지)파일 저장 메소드
  // 경로: TimeCapsule/files/images/{파일 이름} or TimeCapsule/files/sounds/{파일 이름}
  public void saveFiles(List<MultipartFile> files) throws IOException {

    if (!files.isEmpty()) {
      for (MultipartFile file : files) {
        // 외부 경로로 디렉토리와 파일을 지정
        File directory = getExternalPath(file);

        // 경로가 존재하지 않으면 새로운 폴더 생성
        if (!directory.exists()) {
          directory.mkdirs();
        }

        // 실제로 저장할 경로 생성
        File save = new File(directory, file.getOriginalFilename());
        System.out.println("저장경로: " + save.getPath());

        // 파일을 지정된 경로에 저장
        file.transferTo(save);
      }
    }
  }

  // 외부 경로 설정 메소드
  private File getExternalPath(MultipartFile file) {

    // 원본 파일의 이름과 확장자 분리
    String fileName = file.getOriginalFilename();
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    String path = externalPath;

    // 확장자에 따른 경로 설정
    if (soundFileExtensions.contains(extension)) {
      path += "/sounds/";
    } else if (imageFileExtensions.contains(extension)) {
      path += "/images/";
    }

    // 경로를 File형으로 반환
    return Paths.get(path).toFile();
  }
}
