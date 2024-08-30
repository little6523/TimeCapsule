package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByUserId(String id);
  Optional<User> findByEmail(String email);
  Optional<User> findByNickname(String nickname);

  boolean existsByUserId(final String id);
  boolean existsByEmail(final String email);
  boolean existsByNickname(final String nickname);
}