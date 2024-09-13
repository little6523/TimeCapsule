package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.*;
import com.ahi.timecapsule.dto.request.StoryContentDTO;
import com.ahi.timecapsule.dto.request.StoryOptionDTO;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.StoryShare;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.exception.StoryNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.repository.StoryRepository;
import com.ahi.timecapsule.repository.StoryShareRepository;
import com.ahi.timecapsule.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoryService {

  private StoryRepository storyRepository;

  private UserRepository userRepository;

  private StoryShareRepository storyShareRepository;

  private final List<String> soundFileExtensions =
      Arrays.asList("mp3", "wav", "ogg", "flac", "aac", "m4a", "wma");

  private final List<String> imageFileExtensions =
      Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff");

  // 업로드 파일 저장 경로를 외부로 설정
  private final String externalPath =
      Paths.get(System.getProperty("user.home"), "files").toString();

  public StoryService(
      StoryRepository storyRepository,
      UserRepository userRepository,
      StoryShareRepository storyShareRepository) {
    this.storyRepository = storyRepository;
    this.userRepository = userRepository;
    this.storyShareRepository = storyShareRepository;
  }

  // 마이 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findUserStories(String userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage = storyRepository.findByUser_UserId(userId, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 공유된 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findSharedStories(String userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<StoryShare> storyShares = storyShareRepository.findByUser_UserId(userId, pageable);

    return storyShares.map(
        storyShare -> {
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
    Story story =
        storyRepository.findById(storyId).orElseThrow(() -> new StoryNotFoundException(storyId));

    return FindStoryResponseDTO.fromEntity(story);
  }

  // 특정 스토리 수정
  @Transactional
  public void updateStory(Long id, UpdateStoryRequestDTO storyRequestDTO) {
    Story existingStory =
        storyRepository.findById(id).orElseThrow(() -> new StoryNotFoundException(id));

    List<User> users =
        storyRequestDTO.getSharedWithUsers().stream()
            .map(
                nickname ->
                    userRepository
                        .findByNickname(nickname)
                        .orElseThrow(
                            () -> new UserNotFoundException("사용자를 찾을 수 없습니다. 닉네임: " + nickname)))
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
  public Page<FindStoryResponseDTO> findMyStoriesByKeyword(
      String userId, String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage =
        storyRepository.findByUser_IdAndTitleContainsOrContentContains(userId, keyword, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 특정 키워드가 제목, 내용에 포함된 공유된 스토리 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findSharedStoriesByKeyword(
      String userId, String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<StoryShare> storyShares =
        storyShareRepository.findByUser_IdAndStory_TitleContainsOrStory_ContentContains(
            userId, keyword, pageable);

    return storyShares.map(
        storyShare -> {
          Story story = storyShare.getStory();
          return FindStoryResponseDTO.fromEntity(story);
        });
  }

  // 특정 키워드가 제목, 내용에 포함된 커뮤니티 목록 조회
  @Transactional(readOnly = true)
  public Page<FindStoryResponseDTO> findCommunityStoriesByKeyword(
      String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Story> storyPage =
        storyRepository.findByIsSharedTrueAndTitleContainsOrContentContains(keyword, pageable);

    return storyPage.map(FindStoryResponseDTO::fromEntity);
  }

  // 스토리 생성 시 필요한 인터뷰(사운드), 사진(이미지)파일 저장 메소드
  @Transactional
  public List<String> saveFiles(List<MultipartFile> files, String userId) throws IOException {
    List<String> filesPath = new ArrayList<>();
    if (!files.isEmpty()) {

      LocalDateTime now = LocalDateTime.now();

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

      int fileIndex = 1;

      for (MultipartFile file : files) {
        // 외부 경로로 디렉토리와 파일을 지정
        File directory = getExternalPath(file);

        // 경로가 존재하지 않으면 새로운 폴더 생성
        if (!directory.exists()) {
          directory.mkdirs();
        }

        // 새로운 파일명 설정
        String timestamp = now.format(formatter);
        String newFileName =
            timestamp + "_" + userId + "_" + fileIndex + "_" + file.getOriginalFilename();

        // 실제로 저장할 경로 생성
        File save = new File(directory, newFileName);

        // 파일을 지정된 경로에 저장
        file.transferTo(save);

        filesPath.add(save.getPath());

        fileIndex++;
      }
    }
    return filesPath;
  }

  @Transactional
  public List<Object> getSoundFile(String fileName) {
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    Path filePath = Paths.get(fileName);
    File file = filePath.toFile();

    if (!file.exists()) {
      return null;
    }

    List<Object> getFile = new ArrayList<>();
    getFile.add(new FileSystemResource(file));
    getFile.add(extension);

    return getFile;
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

  @Transactional
  public StoryDTO saveStory(
      StoryOptionDTO storyOptionDTO,
      StoryContentDTO storyContentDTO,
      List<String> filesPath,
      String userId) {

    // User 객체 조회
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("일치하는 User가 존재하지 않습니다."));

    // StoryDTO 생성 후, Entity로 변환 및 저장
    StoryDTO storyDTO =
        StoryDTO.builder()
            .dialect(storyOptionDTO.getDialect())
            .speaker(storyOptionDTO.getSpeaker())
            .soundFile(storyOptionDTO.getSoundFile())
            .title(storyContentDTO.getTitle())
            .isShared(storyContentDTO.isShared())
            .content(storyContentDTO.getContent())
            .userDTO(UserDTO.fromEntity(user))
            .build();

    Story story = storyDTO.toEntity();
    Story savedStory = storyRepository.save(story); // 먼저 Story 저장

    // SharedUsers 저장
    if (storyContentDTO.getSharedUsers() != null) {
      for (String sharedUserNickname : storyContentDTO.getSharedUsers()) {
        // 공유할 유저 조회
        User sharedUser =
            userRepository
                .findByNickname(sharedUserNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        // StoryShareDTO 생성 후, StoryShare 저장
        StoryShare storyShare =
            StoryShare.builder()
                .story(savedStory) // 이미 저장된 Story 사용
                .user(sharedUser) // 공유 대상 유저
                .build();

        storyShareRepository.save(storyShare); // StoryShare 저장
      }
    }

    return StoryDTO.fromEntity(story);
  }
}
