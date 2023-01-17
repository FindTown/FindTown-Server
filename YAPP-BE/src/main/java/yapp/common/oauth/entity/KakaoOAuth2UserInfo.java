package yapp.common.oauth.entity;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    String email = (String) kakaoAccount.get("email");
    log.info("카카오 이메일 : {}", email);
    return email;
  }

  @Override
  public String getNickname() {
    Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
    String nickName = (String) properties.get("nickname");
    ;
    log.info("카카오 프로필 닉네임 : {}", nickName);
    return nickName;
  }
}
