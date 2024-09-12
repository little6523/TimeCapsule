package com.ahi.timecapsule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ahi.timecapsule.dto.notice.request.NoticeCreateDTO;
import com.ahi.timecapsule.dto.notice.request.NoticeUpdateDTO;
import com.ahi.timecapsule.dto.notice.response.NoticeDetailDTO;
import com.ahi.timecapsule.entity.Notice;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.exception.NoticeNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.repository.NoticeRepository;
import com.ahi.timecapsule.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private NoticeRepository noticeRepository;

  @InjectMocks private NoticeService noticeService;

  private final LocalDateTime fixedTime = LocalDateTime.of(2024, 9, 4, 1, 0);

  private User createUser() {
    return User.builder().userId("testUser").nickname("Test User").build();
  }

  @Test
  @DisplayName("공지사항 생성 테스트")
  public void testCreateNotice() {
    User user = createUser();
    String userNickname = "Test User";
    NoticeCreateDTO createDTO = new NoticeCreateDTO("Test Title", "Test Content");

    Notice savedNotice =
        Notice.builder()
            .id(1L)
            .title("Test Title")
            .content("Test Content")
            .createdAt(fixedTime)
            .updatedAt(fixedTime)
            .user(user)
            .build();

    when(userRepository.findByNickname(userNickname)).thenReturn(Optional.of(user));
    when(noticeRepository.save(any(Notice.class))).thenReturn(savedNotice);

    NoticeDetailDTO result = noticeService.createNotice(createDTO, "Test User");

    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Title");
    assertThat(result.getContent()).isEqualTo("Test Content");
    assertThat(result.getUserNickname()).isEqualTo(userNickname);
    assertThat(result.getCreatedAt()).isEqualTo(fixedTime);
    assertThat(result.getUpdatedAt()).isEqualTo(fixedTime);

    verify(userRepository).findByNickname(userNickname);
    verify(noticeRepository).save(any(Notice.class));
  }

  @Test
  @DisplayName("존재하지 않는 닉네임 공지사항 생성 예외 발생 테스트")
  public void testCreateNoticeWithException() {
    String nonExistentUserNickname = "Existent User";
    NoticeCreateDTO createDTO = new NoticeCreateDTO("Test Title", "Test Content");

    when(userRepository.findByNickname(nonExistentUserNickname)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> noticeService.createNotice(createDTO, nonExistentUserNickname))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("사용자를 찾을 수 없습니다." + nonExistentUserNickname);

    verify(userRepository).findByNickname(nonExistentUserNickname);
  }

  @Test
  @DisplayName("공지사항 상세조회 테스트")
  public void testGetDetailNotice() {
    Long noticeId = 1L;
    User user = createUser();

    Notice notice =
        Notice.builder()
            .id(noticeId)
            .title("Test Title")
            .content("Test Content")
            .createdAt(fixedTime)
            .updatedAt(fixedTime)
            .user(user)
            .build();

    when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

    NoticeDetailDTO result = noticeService.getDetailNotice(noticeId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(noticeId);
    assertThat(result.getTitle()).isEqualTo("Test Title");
    assertThat(result.getContent()).isEqualTo("Test Content");
    assertThat(result.getCreatedAt()).isEqualTo(fixedTime);
    assertThat(result.getUpdatedAt()).isEqualTo(fixedTime);

    verify(noticeRepository).findById(noticeId);
  }

  @Test
  @DisplayName("존재하지 않는 공지사항 조회 예외 발생 테스트")
  public void testGetDetailNoticeNotFound() {
    Long nonExistentNoticeId = 1L;

    assertThatThrownBy(() -> noticeService.getDetailNotice(nonExistentNoticeId))
        .isInstanceOf(NoticeNotFoundException.class)
        .hasMessage("해당 공지사항은 존재하지 않습니다.");

    verify(noticeRepository).findById(nonExistentNoticeId);
  }

  @Test
  @DisplayName("공지사항 수정 테스트")
  public void testUpdateNotice() {
    Long noticeId = 1L;
    User user = createUser();
    NoticeUpdateDTO updateDTO = new NoticeUpdateDTO(noticeId, "Updated Title", "Updated Content");

    Notice existingNotice =
        Notice.builder()
            .id(noticeId)
            .title("Old Title")
            .content("Old Content")
            .createdAt(fixedTime)
            .updatedAt(fixedTime)
            .user(user)
            .build();
    Notice updatedNotice =
        Notice.builder()
            .id(noticeId)
            .title("Updated Title")
            .content("Updated Content")
            .createdAt(fixedTime)
            .updatedAt(fixedTime.plusDays(1))
            .user(user)
            .build();

    when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(existingNotice));
    when(noticeRepository.save(any(Notice.class))).thenReturn(updatedNotice);

    NoticeDetailDTO result = noticeService.updateNotice(noticeId, updateDTO);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(noticeId);
    assertThat(result.getTitle()).isEqualTo("Updated Title");
    assertThat(result.getContent()).isEqualTo("Updated Content");
    assertThat(result.getCreatedAt()).isEqualTo(fixedTime);
    assertThat(result.getUpdatedAt()).isEqualTo(fixedTime.plusDays(1));

    verify(noticeRepository).findById(noticeId);
    verify(noticeRepository).save(any(Notice.class));
  }

  @Test
  @DisplayName("공지사항 삭제 테스트")
  public void testDeleteNotice() {
    Long noticeId = 1L;
    User user = createUser();

    Notice notice =
        Notice.builder()
            .id(noticeId)
            .title("Test Title")
            .content("Test Content")
            .createdAt(fixedTime)
            .updatedAt(fixedTime)
            .user(user)
            .build();

    when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

    noticeService.deleteNotice(noticeId);

    verify(noticeRepository).findById(noticeId);
    verify(noticeRepository).delete(notice);
  }
}
