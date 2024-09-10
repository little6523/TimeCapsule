package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {


}
