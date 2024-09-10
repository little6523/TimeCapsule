package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByNickname(String nickname);
}
