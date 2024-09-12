package com.ahi.timecapsule.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ahi.timecapsule.entity.Notice;
import com.ahi.timecapsule.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NoticeRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Autowired private NoticeRepository noticeRepository;

  private User user;

  private final LocalDateTime fixedTime = LocalDateTime.of(2024, 9, 3, 0, 0);

  @BeforeEach
  public void setUp() {
    noticeRepository.deleteAll();
    userRepository.deleteAll();

    user = createUser();
    userRepository.save(user);
  }

  private User createUser() {
    return User.builder()
        .userId("testUser")
        .password("password")
        .email("test@example.com")
        .nickname("tester")
        .role(1)
        .build();
  }

  private Notice createNotice(String title, String content, LocalDateTime dateTime) {
    return Notice.builder()
        .title(title)
        .content(content)
        .createdAt(dateTime)
        .updatedAt(dateTime)
        .user(user)
        .build();
  }

  @Test
  @DisplayName("전체 공지사항 조회 테스트")
  public void testFindAllByOrderByCreatedAtDesc() {
    Notice notice1 = createNotice("test1", "content1", fixedTime);
    Notice notice2 = createNotice("test2", "content2", fixedTime.plusSeconds(1));

    noticeRepository.saveAll(List.of(notice1, notice2));

    Pageable pageable = PageRequest.of(0, 10);
    Page<Notice> result = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);

    assertThat(result).isNotNull().hasSize(2).containsExactly(notice2, notice1);
  }

  @Test
  @DisplayName("제목, 내용 검색 공지사항 조회 테스트")
  public void testFindByTitleOrContentOrderByCreatedAtDesc() {
    Notice notice1 = createNotice("test", "content", fixedTime);
    Notice notice2 = createNotice("test1", "content1", fixedTime.plusHours(1));
    Notice notice3 = createNotice("java", "javaContent", fixedTime.plusDays(2));

    noticeRepository.saveAll(List.of(notice1, notice2, notice3));

    Pageable pageable = PageRequest.of(0, 10);
    Page<Notice> result =
        noticeRepository.findByTitleOrContentOrderByCreatedAtDesc("test", "content", pageable);

    assertThat(result).isNotNull().hasSize(3).containsExactly(notice3, notice2, notice1);
  }
}
