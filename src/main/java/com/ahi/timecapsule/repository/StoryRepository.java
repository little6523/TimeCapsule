package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoryRepository extends JpaRepository<Story, Integer> {
  Page<Story> findByUser_Id(String id, Pageable pageable);

  Page<Story> findByStoryShares_User_Id(String id, Pageable pageable);

  @Query("SELECT s FROM Story s WHERE s.user.id = :id AND (s.title LIKE CONCAT('%', :keyword, '%') OR s.content LIKE CONCAT('%', :keyword, '%'))")
  Page<Story> findByUser_IdAndTitleContainsOrContentContains(@Param("id") String id,
                                                             @Param("keyword") String keyword,
                                                             Pageable pageable);

  @Query("SELECT s FROM Story s JOIN s.storyShares ss WHERE ss.user.id = :userId AND (s.title LIKE CONCAT('%', :keyword, '%') OR s.content LIKE CONCAT('%', :keyword, '%'))")
  Page<Story> findByStoryShares_User_IdAndTitleContainsOrContentContains(@Param("userId") String userId,
                                                                         @Param("keyword") String keyword,
                                                                         Pageable pageable);

}
