package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.*;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.exception.StoryNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.repository.StoryRepository;
import com.ahi.timecapsule.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
  public Page<FindStoryResponseDTO> findUserStories(String userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByUser_Id(userId, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 공유된 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findSharedStories(String userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByStoryShares_User_Id(userId, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 스토리 상세 조회
  @Transactional(readOnly = true)
  public FindStoryResponseDTO getStoryById(int storyId) {
    Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new StoryNotFoundException(storyId));

    return FindStoryResponseDTO.fromEntity(story);
  }

  // 특정 스토리 수정
  @Transactional
  public void updateStory(Integer id, UpdateStoryRequestDTO storyRequestDTO) {
    Story existingStory = storyRepository.findById(id)
            .orElseThrow(() -> new StoryNotFoundException(id));

    List<User> users = storyRequestDTO.getSharedWithUsers().stream()
            .map(userId -> userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId)))
            .toList();

    storyRequestDTO.toEntity(existingStory, users);
    storyRepository.save(existingStory);
  }

  // 특정 스토리 삭제
  @Transactional
  public void deleteStoryById(int storyId) {
    storyRepository.findById(storyId).orElseThrow(() -> new StoryNotFoundException(storyId));
    storyRepository.deleteById(storyId);
  }

  // 특정 키워드가 제목, 내용에 포함된 마이 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findMyStoriesByKeyword (String userId, String keyword,  int page, int size){
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByUser_IdAndTitleContainsOrContentContains(userId, keyword, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 특정 키워드가 제목, 내용에 포함된 공유된 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findSharedStoriesByKeyword (String userId, String keyword,  int page, int size){
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByStoryShares_User_IdAndTitleContainsOrContentContains(userId, keyword, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }
}


