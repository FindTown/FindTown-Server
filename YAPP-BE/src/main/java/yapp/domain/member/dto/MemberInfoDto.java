package yapp.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import yapp.common.oauth.entity.ProviderType;
import yapp.common.oauth.entity.RoleType;
import yapp.domain.member.entitiy.Resident;

@Getter
@Setter
public class MemberInfoDto {
  private String memberId;
  private String email;
  private String nickname;
  private ProviderType providerType;
  private RoleType roleType;
  private String interestTownId;
  private Resident resident;
  private String useAgreeYn;
  private String privacyAgreeYn;
}
