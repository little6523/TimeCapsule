package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.*;
import com.ahi.timecapsule.entity.Image;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.StoryShare;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.repository.ImageRepository;
import com.ahi.timecapsule.repository.StoryRepository;
import com.ahi.timecapsule.repository.StoryShareRepository;
import com.ahi.timecapsule.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StoryService {
  private final StoryRepository storyRepository;
  private final UserRepository userRepository;

  public StoryService(StoryRepository storyRepository, UserRepository userRepository) {
    this.storyRepository = storyRepository;
    this.userRepository = userRepository;
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

  // 특정 스토리 수정
  @Transactional
  public void updateStory(Integer id, UpdateStoryRequestDTO storyRequestDTO) {
    Story existingStory = storyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("스토리를 찾을 수 없습니다." + id));
                              // 예외 처리 수정 필요

    List<StoryShare> updatedStoryShares = createUpdatedStoryShares(storyRequestDTO, existingStory);
    List<Image> updatedImages = createUpdatedImages(storyRequestDTO, existingStory);

    Story updatedStory = Story.updateStory(
            existingStory,
            storyRequestDTO.getTitle(),
            storyRequestDTO.getContent(),
            storyRequestDTO.isShared(),
            updatedStoryShares,
            updatedImages
    );

    storyRepository.save(updatedStory);
  }

  private static List<Image> createUpdatedImages(UpdateStoryRequestDTO storyRequestDTO, Story existingStory) {
    return storyRequestDTO.getImages().stream()
            .map(url -> Image.builder()
                    .story(existingStory)
                    .url(url)
                    .build())
            .toList();
  }

  private List<StoryShare> createUpdatedStoryShares(UpdateStoryRequestDTO storyRequestDTO, Story existingStory) {
    return storyRequestDTO.getSharedWithUsers().stream()
            .map(userId -> {
              User user = userRepository.findById(userId)
                      .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

              return StoryShare.builder().story(existingStory).user(user).build();
            })
            .toList();
  }

  // 특정 스토리 삭제
  @Transactional
  public void deleteStoryById(int storyId) {
    storyRepository.deleteById(storyId);
  }

  // 마이 스토리에서 특정 키워드가 제목, 내용에 포함된 스토리 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findMyStoryByKeyword (String userId, String keyword,  int page, int size){
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByUser_IdAndTitleContainsOrContentContains(userId, keyword, pageable);

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

  // 공유된 스토리에서 특정 키워드가 제목, 내용에 포함된 스토리 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findSharedStoryByKeyword (String userId, String keyword,  int page, int size){
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByStoryShares_User_IdAndTitleContainsOrContentContains(userId, keyword, pageable);

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
}


