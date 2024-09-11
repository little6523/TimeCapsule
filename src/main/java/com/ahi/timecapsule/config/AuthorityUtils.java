package com.ahi.timecapsule.config;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthorityUtils {
  // 숫자 권한을 문자열 권한으로 변환하는 메서드
  public static List<GrantedAuthority> convertRolesToAuthorities(int role) {
    // 예: 숫자 1은 ROLE_USER, 2는 ROLE_ADMIN으로 매핑
    String roleName =
        switch (role) {
          case 1 -> "ROLE_USER";
          case 2 -> "ROLE_ADMIN";
            // 필요한 다른 권한 추가 가능
          default -> throw new IllegalArgumentException("잘못된 권한 값: " + role);
        };
    return List.of(new SimpleGrantedAuthority(roleName));
  }
}
