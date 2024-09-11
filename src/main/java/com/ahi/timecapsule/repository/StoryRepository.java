package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoryRepository extends JpaRepository<Story, Long> {

  Page<Story> findByUser_UserId(String userId, Pageable pageable);

  @Query(
      "SELECT s FROM Story s WHERE s.user.userId = :id AND (s.title LIKE CONCAT('%', :keyword, '%') OR s.content LIKE CONCAT('%', :keyword, '%'))")
  Page<Story> findByUser_IdAndTitleContainsOrContentContains(
      @Param("id") String id, @Param("keyword") String keyword, Pageable pageable);

  Page<Story> findByIsSharedTrue(Pageable pageable);

  @Query(
      "SELECT s FROM Story s WHERE s.isShared = true AND (s.title LIKE CONCAT('%', :keyword, '%') OR s.content LIKE CONCAT('%', :keyword, '%'))")
  Page<Story> findByIsSharedTrueAndTitleContainsOrContentContains(
      @Param("keyword") String keyword, Pageable pageable);
}
