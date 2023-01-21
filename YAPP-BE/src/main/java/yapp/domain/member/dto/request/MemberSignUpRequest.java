package yapp.domain.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import yapp.common.oauth.entity.ProviderType;
import yapp.domain.member.entitiy.Resident;

@Getter
@Setter
@Builder
public class MemberSignUpRequest {
  private String nickname;
  private ProviderType providerType;
  private Long objectId;
  private Resident resident;
  private String useAgreeYn;
  private String privacyAgreeYn;
}
