package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.ImageDTO;
import com.ahi.timecapsule.entity.Image;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.exception.NotFoundException;
import com.ahi.timecapsule.repository.ImageRepository;
import com.ahi.timecapsule.repository.StoryRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ImageService {
  private final ImageRepository imageRepository;
  private final StoryRepository storyRepository;

  public ImageService(ImageRepository imageRepository, StoryRepository storyRepository) {
    this.imageRepository = imageRepository;
    this.storyRepository = storyRepository;
  }

  public String encodeImageToBase64(String imagePath) throws IOException {
    System.out.println("저장된 이미지 주소: " + imagePath);
    File imageFile = new File(imagePath);
    byte[] fileContent = Files.readAllBytes(imageFile.toPath());
    return Base64.getEncoder().encodeToString(fileContent);
  }

  public void updateImages(List<Long> deletedImages, List<String> addedImages, Long storyId) {
    List<ImageDTO> updatedImages = new ArrayList<>();
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
