package com.ahi.timecapsule;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/notices", "/notices/*")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/notices/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/notices/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/notices/form", "/notices/*/edit")
                    .hasRole("ADMIN")
                    .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    return http.build();
  }
}
