package yapp.common.oauth.entity;

import java.util.Map;

public class OAuth2UserInfoFactory {
  public static OAuth2UserInfo getOAuth2UserInfo(
    ProviderType providerType,
    Map<String, Object> attributes
  ) {
    switch (providerType) {
      case KAKAO:
        return new KakaoOAuth2UserInfo(attributes);
      default:
        throw new IllegalArgumentException("유효하지 않는 계정 타입 입니다.");
    }
  }
}
