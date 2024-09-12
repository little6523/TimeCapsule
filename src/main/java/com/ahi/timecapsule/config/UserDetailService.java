package com.ahi.timecapsule.config;

import static com.ahi.timecapsule.config.AuthorityUtils.convertRolesToAuthorities;

import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + userId));

    // UserDetails 객체로 변환
    return org.springframework.security.core.userdetails.User.withUsername(user.getUserId())
        //						.password(user.getPassword())

        .password(user.getPassword() != null ? user.getPassword() : "") // 추가
        .authorities(convertRolesToAuthorities(user.getRole())) // 역할 설정 필요 시 수정
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }
}
