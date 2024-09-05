package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
