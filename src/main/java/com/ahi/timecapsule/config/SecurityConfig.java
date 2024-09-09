package com.ahi.timecapsule.config;

import com.ahi.timecapsule.oauth.CustomOAuth2FailureHandler;
import com.ahi.timecapsule.oauth.CustomOAuth2SuccessHandler;
import com.ahi.timecapsule.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailService userDetailService;
  private final PasswordEncoder passwordEncoder;
  private final JwtAuthenticationEntryPoint unauthorizedHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
  private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //        http.formLogin(cnf ->
    // cnf.loginPage("/login").usernameParameter("userId").passwordParameter("password").permitAll());
    http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
        .exceptionHandling(
            exceptions -> exceptions.authenticationEntryPoint(unauthorizedHandler)) // 예외 처리 설정
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안함
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/login",
                        "/signUpPage",
                        "/signUp",
                        "/api/users/**",
                        "/users/**",
                        "/valid-token",
                        "/oauth/login")
                    .permitAll() // 특정 경로 허용
                    .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**")
                    .permitAll()
                    .anyRequest()
                    .permitAll() // 나머지 요청 전부 허용
            )
        .oauth2Login(
            oauth2Login ->
                oauth2Login
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                    .successHandler(customOAuth2SuccessHandler)
                    .failureHandler(customOAuth2FailureHandler))
        .addFilterBefore(
            jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
    http.headers(
        headers ->
            headers
                .addHeaderWriter(
                    new StaticHeadersWriter(
                        "X-XSS-Protection", "1; mode=block")) // XSS 필터 활성화 및 차단 모드 설정
                //                .addHeaderWriter(new
                // StaticHeadersWriter("Content-Security-Policy", "default-src 'self'; script-src
                // 'self'; object-src 'none'; frame-ancestors 'none'; form-action 'self'; base-uri
                // 'self';")) // CSP 설정
                .addHeaderWriter(
                    new StaticHeadersWriter("X-Content-Type-Options", "nosniff")) // MIME 타입 스니핑 방지
        );
    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailService); // UserService를 UserDetailsService로 사용
    authProvider.setPasswordEncoder(passwordEncoder);
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
