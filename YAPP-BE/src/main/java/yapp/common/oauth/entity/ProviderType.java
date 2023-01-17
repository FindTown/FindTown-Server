package yapp.common.oauth.entity;

import lombok.Getter;

@Getter
public enum ProviderType {
  KAKAO,
  APPLE;

  public static ProviderType of(String type) {
    return ProviderType.valueOf(type.toUpperCase());
  }
}
