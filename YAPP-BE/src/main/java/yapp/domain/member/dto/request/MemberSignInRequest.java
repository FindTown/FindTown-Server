package yapp.domain.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import yapp.common.oauth.entity.ProviderType;

@Getter
@Setter
@Builder
public class MemberSignInRequest {
  String memberId;
  ProviderType providerType;
  String email;
  String nickname;
}
