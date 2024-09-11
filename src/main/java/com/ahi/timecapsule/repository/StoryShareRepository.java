package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.StoryShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoryShareRepository extends JpaRepository<StoryShare, Long> {
  Page<StoryShare> findByUser_UserId(String userId, Pageable pageable);

  @Query(
      "SELECT ss FROM StoryShare ss WHERE ss.user.userId = :userId AND (ss.story.title LIKE %:keyword% OR ss.story.content LIKE %:keyword%)")
  Page<StoryShare> findByUser_IdAndStory_TitleContainsOrStory_ContentContains(
      @Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);
}
