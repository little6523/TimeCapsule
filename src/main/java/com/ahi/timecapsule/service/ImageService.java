package com.ahi.timecapsule.service;

import com.ahi.timecapsule.entity.Image;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.exception.NotFoundException;
import com.ahi.timecapsule.repository.ImageRepository;
import com.ahi.timecapsule.repository.StoryRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageService {
  private final ImageRepository imageRepository;
  private final StoryRepository storyRepository;

  public ImageService(ImageRepository imageRepository, StoryRepository storyRepository) {
    this.imageRepository = imageRepository;
    this.storyRepository = storyRepository;
  }

  // 이미지 base64 인코딩
  @Transactional
  public String encodeImageToBase64(String imagePath) throws IOException {
    File imageFile = new File(imagePath);
    byte[] fileContent = Files.readAllBytes(imageFile.toPath());
    return Base64.getEncoder().encodeToString(fileContent);
  }

  // 이미지 저장
  @Transactional
  public void saveImages(List<String> addedImages, Long storyId) {
    Story story =
        storyRepository
            .findById(storyId)
            .orElseThrow(() -> new IllegalArgumentException("스토리 id가 존재하지 않습니다."));

    for (String addedImage : addedImages) {
      Image image = Image.builder().story(story).url(addedImage).build();
      imageRepository.save(image);
    }
  }

  // 이미지 업데이트
  @Transactional
  public void updateImages(List<Long> deletedImages, List<String> addedImages, Long storyId) {
    for (Long imageId : deletedImages) {
      imageRepository.deleteById(imageId);
    }

    Story story = storyRepository.findById(storyId).orElseThrow(NotFoundException::new);
    for (String addedImage : addedImages) {
      Image image = Image.builder().story(story).url(addedImage).build();
      imageRepository.save(image);
    }
  }
}
