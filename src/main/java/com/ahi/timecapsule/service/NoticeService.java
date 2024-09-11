package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.notice.request.NoticeCreateDTO;
import com.ahi.timecapsule.dto.notice.request.NoticeUpdateDTO;
import com.ahi.timecapsule.dto.notice.response.NoticeDetailDTO;
import com.ahi.timecapsule.dto.notice.response.NoticeListDTO;
import com.ahi.timecapsule.entity.Notice;
import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.exception.NoticeNotFoundException;
import com.ahi.timecapsule.exception.UserNotFoundException;
import com.ahi.timecapsule.repository.NoticeRepository;
import com.ahi.timecapsule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

  private final NoticeRepository noticeRepository;
  private final UserRepository userRepository;

  // 공지사항 생성
  @Transactional
  public NoticeDetailDTO createNotice(NoticeCreateDTO createDTO, String userNickname) {
    User user =
        userRepository
            .findByNickname(userNickname)
            .orElseThrow(() -> new UserNotFoundException(userNickname));

    Notice notice = createDTO.toEntity(user);
    Notice savedNotice = noticeRepository.save(notice);

    return NoticeDetailDTO.fromEntity(savedNotice);
  }

  // 공지사항 상세 조회
  @Transactional(readOnly = true)
  public NoticeDetailDTO getDetailNotice(Integer id) {
    Notice notice = noticeRepository.findById(id).orElseThrow(NoticeNotFoundException::new);

    return NoticeDetailDTO.fromEntity(notice);
  }

  // 공지사항 수정
  @Transactional
  public NoticeDetailDTO updateNotice(Integer id, NoticeUpdateDTO updateDTO) {
    Notice notice = noticeRepository.findById(id).orElseThrow(NoticeNotFoundException::new);

    notice = updateDTO.toEntity(notice);
    Notice updatedNotice = noticeRepository.save(notice);

    return NoticeDetailDTO.fromEntity(updatedNotice);
  }

  // 공지사항 삭제
  @Transactional
  public void deleteNotice(Integer id) {
    Notice notice = noticeRepository.findById(id).orElseThrow(NoticeNotFoundException::new);

    noticeRepository.delete(notice);
  }

  // 공지사항 전체 목록 조회
  @Transactional(readOnly = true)
  public Page<NoticeListDTO> getAllNotices(Pageable pageable) {
    Page<Notice> noticePage = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);

    return noticePage.map(NoticeListDTO::fromEntity);
  }

  // 공지사항 제목, 내용 검색 목록 조회
  @Transactional(readOnly = true)
  public Page<NoticeListDTO> searchNotices(String title, String content, Pageable pageable) {
    Page<Notice> noticePage =
        noticeRepository.findByTitleOrContentOrderByCreatedAtDesc(title, content, pageable);

    return noticePage.map(NoticeListDTO::fromEntity);
  }
}
