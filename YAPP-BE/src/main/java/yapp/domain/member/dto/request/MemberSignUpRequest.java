package yapp.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yapp.common.oauth.entity.ProviderType;
import yapp.domain.member.entity.Resident;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpRequest {
  private String memberId;
  private String email;
  private String nickname;
  private ProviderType providerType;
  private Long objectId;
  private Resident resident;
  private boolean useAgreeYn;
  private boolean privacyAgreeYn;
}
