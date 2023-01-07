package yapp.common.oauth.entity;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

  public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getMemberId() {
    return attributes.get("id").toString();
  }
  
  @Override
  public String getEmail() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    return (String) kakaoAccount.get("email");
  }

  @Override
  public String getNickname() {
    return (String) attributes.get("profile_nickname");
  }
}
