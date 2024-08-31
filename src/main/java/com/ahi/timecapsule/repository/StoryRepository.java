package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Integer> {
  Page<Story> findByUser_Id(String id, Pageable pageable);

  Page<Story> findByStoryShares_User_Id(String id, Pageable pageable);
}
