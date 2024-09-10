package com.ahi.timecapsule.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailService customUserDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String jwt = jwtTokenProvider.resolveToken(request); // 헤더에서 jwt 토큰 받아오기

    // 토큰이 유효할 때
    if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
      String username = jwtTokenProvider.getUsernameFromJwtToken(jwt);
      UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
      var authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 다음 filter 실행
    filterChain.doFilter(request, response);
  }

  //  private String getJwtFromRequest(HttpServletRequest request) {
  //    // JWT가 쿠키에 저장된 경우
  //    return Arrays.stream(request.getCookies())
  //            .filter(cookie -> "jwt".equals(cookie.getName()))
  //            .map(Cookie::getValue)
  //            .findFirst()
  //            .orElse(null);
  //  }

}
