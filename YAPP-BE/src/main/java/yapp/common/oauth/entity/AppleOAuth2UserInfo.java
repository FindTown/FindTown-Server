package yapp.common.oauth.entity;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

  public AppleOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getMemberId() {
    return null;
  }

  @Override
  public String getNickname() {
    return null;
  }

  @Override
  public String getEmail() {
    return null;
  }
}
