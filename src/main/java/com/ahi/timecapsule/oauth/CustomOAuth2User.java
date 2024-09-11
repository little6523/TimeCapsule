package com.ahi.timecapsule.oauth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {
  private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2User.class);
  private final OAuth2UserInfo oAuth2UserInfo;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomOAuth2User(OAuth2UserInfo oAuth2UserInfo) {
    this.oAuth2UserInfo = oAuth2UserInfo;
    this.authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    logger.debug("CustomOAuth2User created for user: {}", oAuth2UserInfo.getName());
  }

  @Override
  public Map<String, Object> getAttributes() {
    return oAuth2UserInfo.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getName() {
    return oAuth2UserInfo.getName();
  }

  public String getEmail() {
    return oAuth2UserInfo.getEmail();
  }

  public String getImageUrl() {
    return oAuth2UserInfo.getImageUrl();
  }

  public String getId() {
    return oAuth2UserInfo.getId();
  }

  @Override
  public String toString() {
    return "CustomOAuth2User{"
        + "id='"
        + getId()
        + '\''
        + ", name='"
        + getName()
        + '\''
        + ", email='"
        + getEmail()
        + '\''
        + ", imageUrl='"
        + getImageUrl()
        + '\''
        + ", authorities="
        + authorities
        + '}';
  }
}
