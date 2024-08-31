package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.FindImageResponseDTO;
import com.ahi.timecapsule.dto.FindSharedResponseDTO;
import com.ahi.timecapsule.dto.FindStoryResponseDTO;
import com.ahi.timecapsule.dto.StoryUserResponseDTO;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.repository.StoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StoryService {
  private final StoryRepository storyRepository;

  public StoryService(StoryRepository storyRepository) {
    this.storyRepository = storyRepository;
  }

  // 마이 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> getUserStories(String userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByUser_Id(userId, pageable);

    return storyPage.map(story -> FindStoryResponseDTO.builder()
            .id(story.getId())
            .title(story.getTitle())
            .content(story.getContent())
            .createdAt(story.getCreatedAt())
            .author(StoryUserResponseDTO.builder()
                    .id(story.getUser().getId())
                    .nickname(story.getUser().getNickname())
                    .build())
            .build());
  }

  // 공유된 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> getSharedStoriesForUser(String userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByStoryShares_User_Id(userId, pageable);

    return storyPage.map(story -> FindStoryResponseDTO.builder()
            .id(story.getId())
            .title(story.getTitle())
            .content(story.getContent())
            .createdAt(story.getCreatedAt())
            .author(StoryUserResponseDTO.builder()
                    .id(story.getUser().getId())
                    .nickname(story.getUser().getNickname())
                    .build())
            .build());
  }

  // 스토리 상세 조회
  @Transactional(readOnly = true)
  public FindStoryResponseDTO getStoryById(int storyId) {
    Optional<Story> storyOpt = storyRepository.findById(storyId);

    if (storyOpt.isEmpty()) {
      throw new RuntimeException("Could not find story with id " + storyId);
    }

    Story story = storyOpt.get();

    // 이미지 조회를 위한 DTO 생성
    List<FindImageResponseDTO> imageResponseDTOs = story.getImages().stream()
            .map(image -> FindImageResponseDTO.builder()
                    .id(image.getId())
                    .url(image.getUrl())
                    .build())
            .toList();

    // 공유자 조회를 위한 DTO 생성
    List<FindSharedResponseDTO> sharedResponseDTOs = story.getStoryShares().stream()
            .map(share -> FindSharedResponseDTO.builder()
                    .id(share.getId())
                    .sharedStory(null)
                    .sharedWithUser(StoryUserResponseDTO.builder()
                            .id(share.getUser().getId())
                            .nickname(share.getUser().getNickname())
                            .build())
                    .build())
            .toList();

    return FindStoryResponseDTO.builder()
            .id(story.getId())
            .title(story.getTitle())
            .content(story.getContent())
            .createdAt(story.getCreatedAt())
            .soundFile(story.getSoundFile())
            .isShared(story.isShared())
            .author(StoryUserResponseDTO.builder()
                    .id(story.getUser().getId())
                    .nickname(story.getUser().getNickname())
                    .build())
            .images(imageResponseDTOs)
            .sharedStories(sharedResponseDTOs)
            .build();
  }


}
