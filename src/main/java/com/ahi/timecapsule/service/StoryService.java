package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.*;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.StoryShare;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.exception.StoryNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.repository.StoryRepository;
import com.ahi.timecapsule.repository.StoryShareRepository;
import com.ahi.timecapsule.repository.UserRepository;
import org.hibernate.annotations.processing.Find;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StoryService {
  private final StoryRepository storyRepository;
  private final UserRepository userRepository;
  private final StoryShareRepository storyShareRepository;

  public StoryService(StoryRepository storyRepository, UserRepository userRepository, StoryShareRepository storyShareRepository) {
    this.storyRepository = storyRepository;
    this.userRepository = userRepository;
    this.storyShareRepository = storyShareRepository;
  }


  private final List<String> soundFileExtensions =
          Arrays.asList("mp3", "wav", "ogg", "flac", "aac", "m4a", "wma");

  private final List<String> imageFileExtensions =
          Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff");

  // 업로드 파일 저장 경로를 외부로 설정
  private final String externalPath =
          Paths.get(System.getProperty("user.home"), "files").toString();


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
    Page<StoryShare> storyShares = storyShareRepository.findByUser_Id(userId, pageable);

    return storyShares.map(storyShare -> {
      Story story = storyShare.getStory();
      return FindStoryResponseDTO.fromEntity(story);
    });
  }

  // 커뮤니티 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findCommunityStories(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByIsSharedTrue(pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 스토리 상세 조회
  @Transactional(readOnly = true)
  public FindStoryResponseDTO getStoryById(Long storyId) {
    Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new StoryNotFoundException(storyId));

    return FindStoryResponseDTO.fromEntity(story);
  }

  // 특정 스토리 수정
  @Transactional
  public void updateStory(Long id, UpdateStoryRequestDTO storyRequestDTO) {
    Story existingStory = storyRepository.findById(id)
            .orElseThrow(() -> new StoryNotFoundException(id));

//    List<User> users = storyRequestDTO.getSharedWithUsers().stream()
//            .map(userId -> userRepository.findById(userId)
//                    .orElseThrow(() -> new UserNotFoundException(userId)))
//            .toList();
    List<User> users = storyRequestDTO.getSharedWithUsers().stream()
            .map(nickname -> userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. 닉네임: "+ nickname)))
            .toList();

    storyRequestDTO.toEntity(existingStory, users);
    storyRepository.save(existingStory);
  }

  // 특정 스토리 삭제
  @Transactional
  public void deleteStoryById(Long storyId) {
    storyRepository.findById(storyId).orElseThrow(() -> new StoryNotFoundException(storyId));
    storyRepository.deleteById(storyId);
  }

  // 특정 키워드가 제목, 내용에 포함된 마이 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findMyStoriesByKeyword(String userId, String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByUser_IdAndTitleContainsOrContentContains(userId, keyword, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 특정 키워드가 제목, 내용에 포함된 공유된 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findSharedStoriesByKeyword(String userId, String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<StoryShare> storyShares = storyShareRepository.findByUser_IdAndStory_TitleContainsOrStory_ContentContains(userId, keyword, pageable);

    return storyShares.map(storyShare -> {
      Story story = storyShare.getStory();
      return FindStoryResponseDTO.fromEntity(story);
    });
  }

  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findCommunityStoriesByKeyword(String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByIsSharedTrueAndTitleContainsOrContentContains(keyword, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 스토리 생성 시 필요한 인터뷰(사운드), 사진(이미지)파일 저장 메소드
  // 경로: TimeCapsule/files/images/{파일 이름} or TimeCapsule/files/sounds/{파일 이름}
  public List<String> saveFiles(List<MultipartFile> files) throws IOException {
    List<String> filesPath = new ArrayList<>();

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

        filesPath.add(save.getPath());
      }
    }
    return filesPath;
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


