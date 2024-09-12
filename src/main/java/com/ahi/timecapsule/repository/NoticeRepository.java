package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

  // 최신 날짜 기준 게시글 나오는 메서드
  Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);

  // 제목 또는 내용 검색 후 최신 날짜 기준으로 나오는 메서드
  @Query(
      "SELECT n FROM Notice n WHERE n.title LIKE %:title% OR n.content LIKE %:content% ORDER BY n.createdAt DESC")
  Page<Notice> findByTitleOrContentOrderByCreatedAtDesc(
      @Param("title") String title, @Param("content") String content, Pageable pageable);
}
