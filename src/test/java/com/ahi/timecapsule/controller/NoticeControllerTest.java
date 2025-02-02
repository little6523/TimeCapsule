package com.ahi.timecapsule.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ahi.timecapsule.config.JwtAuthenticationFilter;
import com.ahi.timecapsule.dto.notice.request.NoticeCreateDTO;
import com.ahi.timecapsule.dto.notice.request.NoticeUpdateDTO;
import com.ahi.timecapsule.dto.notice.response.NoticeDetailDTO;
import com.ahi.timecapsule.dto.notice.response.NoticeListDTO;
import com.ahi.timecapsule.service.NoticeService;
import com.ahi.timecapsule.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@WebMvcTest(value = NoticeController.class)
public class NoticeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private NoticeService noticeService;

  @MockBean private UserService userService;

  @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  public void setup() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".jsp");

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(new NoticeController(noticeService, userService))
            .setViewResolvers(viewResolver)
            .build();
  }

  private final LocalDateTime fixedTime = LocalDateTime.of(2024, 9, 4, 0, 0);

  private NoticeDetailDTO createNoticeDetailDTO(Long id, String title, String content) {
    return NoticeDetailDTO.builder()
        .id(id)
        .title(title)
        .content(content)
        .createdAt(fixedTime)
        .updatedAt(fixedTime)
        .userId("testUser")
        .userNickname("admin")
        .build();
  }

  @Test
  @DisplayName("전체 공지사항 목록 조회 테스트")
  public void testGetNoticeList() throws Exception {
    List<NoticeListDTO> notices =
        List.of(
            new NoticeListDTO(1L, "Test Title 1", fixedTime, fixedTime, "admin"),
            new NoticeListDTO(2L, "Test Title 2", fixedTime, fixedTime, "admin"));

    Page<NoticeListDTO> noticePage = new PageImpl<>(notices, PageRequest.of(0, 10), 2);

    when(noticeService.getAllNotices(any(Pageable.class))).thenReturn(noticePage);

    mockMvc
        .perform(get("/notices").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("notices"))
        .andExpect(model().attribute("notices", noticePage))
        .andExpect(model().attributeDoesNotExist("searchTerm"))
        .andExpect(view().name("notice/list"));

    verify(noticeService).getAllNotices(any(Pageable.class));
  }

  @Test
  @DisplayName("공지사항 검색 테스트")
  public void testGetNoticeListWithSearchTerm() throws Exception {
    List<NoticeListDTO> notices =
        List.of(new NoticeListDTO(1L, "Test Title 1", fixedTime, fixedTime, "admin"));

    Page<NoticeListDTO> noticePage = new PageImpl<>(notices, PageRequest.of(0, 10), 1);

    when(noticeService.searchNotices(eq("Test"), eq("Test"), any(Pageable.class)))
        .thenReturn(noticePage);

    mockMvc
        .perform(get("/notices").param("page", "0").param("size", "10").param("searchTerm", "Test"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("notices"))
        .andExpect(model().attribute("notices", noticePage))
        .andExpect(model().attributeExists("searchTerm"))
        .andExpect(model().attribute("searchTerm", "Test"))
        .andExpect(view().name("notice/list"));

    verify(noticeService).searchNotices(eq("Test"), eq("Test"), any(Pageable.class));
  }

  @Test
  @DisplayName("특정 공지사항 상세 조회 테스트")
  public void testFindNoticeDetail() throws Exception {
    Long noticeId = 1L;
    NoticeDetailDTO noticeDetail = createNoticeDetailDTO(noticeId, "Test Title", "Test Content");
    when(noticeService.getDetailNotice(noticeId)).thenReturn(noticeDetail);

    mockMvc
        .perform(get("/notices/{id}", noticeId))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("notice"))
        .andExpect(model().attribute("notice", noticeDetail))
        .andExpect(view().name("notice/detail"));

    verify(noticeService).getDetailNotice(noticeId);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("공지사항 생성 폼 조회 테스트")
  public void testGetCreateNoticeForm() throws Exception {
    mockMvc
        .perform(get("/notices/form"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("noticeForm"))
        .andExpect(view().name("notice/form"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("공지사항 생성 테스트")
  public void testCreateNotice() throws Exception {
    String userId = "testUser";
    String userNickname = "admin";
    NoticeDetailDTO createdNotice = createNoticeDetailDTO(1L, "Test Title", "Test Content");

    when(userService.getNicknameByUserId(userId)).thenReturn(userNickname);
    when(noticeService.createNotice(any(NoticeCreateDTO.class), eq(userNickname)))
        .thenReturn(createdNotice);

    mockMvc
        .perform(
            post("/notices")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userId", userId)
                .param("title", "Test Title")
                .param("content", "Test Content"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/notices/" + createdNotice.getId()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("공지사항 생성 유효성 검사 실패 테스트")
  public void testCreateNotice_ValidationFailure() throws Exception {
    mockMvc
        .perform(
            post("/notices")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userId", "12345")
                .param("title", "")
                .param("content", "Test Content"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("noticeForm", "title"))
        .andExpect(view().name("notice/form"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("공지사항 수정 폼 조회 테스트")
  public void testGetUpdateNoticeForm() throws Exception {
    Long noticeId = 1L;
    NoticeDetailDTO noticeDetail = createNoticeDetailDTO(noticeId, "Test Title", "Test Content");

    when(noticeService.getDetailNotice(noticeId)).thenReturn(noticeDetail);

    mockMvc
        .perform(get("/notices/{id}/edit", noticeId))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("noticeForm"))
        .andExpect(model().attribute("noticeForm", noticeDetail))
        .andExpect(view().name("notice/edit"));

    verify(noticeService).getDetailNotice(noticeId);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("공지사항 수정 테스트")
  public void testUpdateNotice() throws Exception {
    Long noticeId = 1L;
    NoticeUpdateDTO updateDTO =
        new NoticeUpdateDTO(noticeId, "Updated Test Title", "Updated Test Content");

    mockMvc
        .perform(
            post("/notices/{id}", noticeId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Updated Test Title")
                .param("content", "Updated Test Content"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/notices/1"));

    verify(noticeService).updateNotice(eq(noticeId), any(NoticeUpdateDTO.class));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("공지사항 삭제 테스트")
  public void testDeleteNotice() throws Exception {
    Long noticeId = 1L;

    mockMvc.perform(delete("/notices/{id}", noticeId)).andExpect(status().isOk());

    verify(noticeService).deleteNotice(noticeId);
  }
}
