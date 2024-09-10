package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.config.JwtTokenProvider;
import com.ahi.timecapsule.dto.notice.request.NoticeCreateDTO;
import com.ahi.timecapsule.dto.notice.request.NoticeUpdateDTO;
import com.ahi.timecapsule.dto.notice.response.NoticeDetailDTO;
import com.ahi.timecapsule.dto.notice.response.NoticeListDTO;
import com.ahi.timecapsule.service.NoticeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;
  private final JwtTokenProvider jwtTokenProvider;

  // 전체 공지사항 목록 조회
  @GetMapping
  public String getNoticeList(
      Model model,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String searchTerm) {
    Pageable pageable = PageRequest.of(page, size);
    Page<NoticeListDTO> notices;

    if (searchTerm != null && !searchTerm.isEmpty()) {
      notices = noticeService.searchNotices(searchTerm, searchTerm, pageable);
    } else {
      notices = noticeService.getAllNotices(pageable);
    }
    model.addAttribute("notices", notices);
    model.addAttribute("searchTerm", searchTerm);

    return "notice/list";
  }

  // 특정 공지사항 상세 조회
  @GetMapping("/{id}")
  public String findNoticeDetail(@PathVariable Integer id, Model model) {
    NoticeDetailDTO noticeDetail = noticeService.getDetailNotice(id);
    model.addAttribute("notice", noticeDetail);
    return "notice/detail";
  }

  // 공지사항 생성 폼 조회
  @GetMapping("/form")
  @RolesAllowed("ADMIN")
  public String getCreateNoticeForm(Model model) {
    model.addAttribute("noticeForm", new NoticeCreateDTO());
    return "notice/form";
  }

  // 공지사항 생성
  @PostMapping
  @RolesAllowed("ADMIN")
  public String createNotice(
      @RequestHeader("Authorization") String bearerToken,
      @Valid @ModelAttribute("noticeForm") NoticeCreateDTO createDTO,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      return "notice/form";
    }

    String token = bearerToken.substring(7);
    String userNickname = jwtTokenProvider.getUsernameFromJwtToken(token);
    NoticeDetailDTO createdNotice = noticeService.createNotice(createDTO, userNickname);

    if (createdNotice == null) {
      model.addAttribute("errorMessage", "공지사항 생성에 실패했습니다. 다시 시도해 주세요.");
      return "notice/form";
    }

    return "redirect:/notices/" + createdNotice.getId();
  }

  // 공지사항 수정 폼 조회
  @GetMapping("/{id}/edit")
  @RolesAllowed("ADMIN")
  public String getUpdateNoticeForm(@PathVariable Integer id, Model model) {
    NoticeDetailDTO noticeDetail = noticeService.getDetailNotice(id);
    model.addAttribute("noticeForm", noticeDetail);
    return "notice/edit";
  }

  // 공지사항 수정
  @PostMapping("/{id}")
  @RolesAllowed("ADMIN")
  public String updateNotice(
      @PathVariable Integer id,
      @Valid @ModelAttribute("noticeForm") NoticeUpdateDTO updateDTO,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "notice/edit";
    }
    noticeService.updateNotice(id, updateDTO);
    return "redirect:/notices/" + id;
  }

  // 공지사항 삭제
  @DeleteMapping("/{id}")
  @RolesAllowed("ADMIN")
  @ResponseBody
  public ResponseEntity<Void> deleteNotice(@PathVariable Integer id) {
    noticeService.deleteNotice(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
