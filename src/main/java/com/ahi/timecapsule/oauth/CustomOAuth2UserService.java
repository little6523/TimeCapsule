package com.ahi.timecapsule.oauth;

import com.ahi.timecapsule.entity.User;
import com.ahi.timecapsule.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
  private final UserRepository usersRepository;

  public CustomOAuth2UserService(UserRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);
    try {
      return processOAuth2User(userRequest, user);
    } catch (EmailAlreadyExistsException ex) {
      throw ex;
    } catch (Exception ex) {
      logger.error("Error processing OAuth2User", ex);
      throw new OAuth2AuthenticationException(ex.getMessage());
    }
  }

  // 제공자 구분 후 사용자 DB 저장 로직 처리
  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2UserInfo oAuth2UserInfo;

    if ("google".equals(registrationId)) {
      oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
    } else if ("kakao".equals(registrationId)) {
      oAuth2UserInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
    } else {
      throw new OAuth2AuthenticationException("죄송합니다! 로그인 위치 " + registrationId + " 아직 지원되지 않습니다.");
    }

    User user = findOrCreateUser(oAuth2UserInfo, registrationId);
    usersRepository.save(user);

    return new CustomOAuth2User(oAuth2UserInfo);
  }

  private User findOrCreateUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
    return usersRepository
        .findByEmail(oAuth2UserInfo.getEmail())
        .map(
            existingUser -> {
              String provider = existingUser.getProvider();
              String upperCaseRegistrationId = registrationId.toUpperCase();

              if (provider == null || !provider.equals(upperCaseRegistrationId)) {
                throw new EmailAlreadyExistsException(
                    "이미 "
                        + (provider != null ? provider : "기존")
                        + " 계정으로 가입된 이메일입니다: "
                        + oAuth2UserInfo.getEmail());
              }

              return updateExistingUser(existingUser, oAuth2UserInfo);
            })
        .orElseGet(() -> createNewUser(oAuth2UserInfo, registrationId));
  }

  // OAuth2.0 계정 업데이트(이미지, 닉네임)
  private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
    return User.builder()
        .userId(existingUser.getUserId())
        .email(existingUser.getEmail())
        .role(existingUser.getRole())
        .provider(existingUser.getProvider())
        .nickname(existingUser.getNickname())
        .build();
  }

  // OAuth2.0 계정 저장
  private User createNewUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
    return User.builder()
        .userId(oAuth2UserInfo.getId())
        .email(oAuth2UserInfo.getEmail())
        .nickname(generateUniqueNickname(oAuth2UserInfo.getName(), null))
        .provider(registrationId.toUpperCase())
        .role(1)
        .build();
  }

  // OAuth2.0 계정 생성 시 중복된 닉네임 처리
  private String generateUniqueNickname(String baseName, String currentNickname) {
    String nickname = baseName;
    int suffix = 1;
    while (usersRepository.existsByNickname(nickname) && !nickname.equals(currentNickname)) {
      nickname = baseName + suffix;
      suffix++;
    }
    return nickname;
  }
}
