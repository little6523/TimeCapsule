package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByStoryId(Long storyId);
}
