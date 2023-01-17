package yapp.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import yapp.common.oauth.entity.ProviderType;

@Getter
@Setter
public class MemberAccountDto {
  private String memberId;
  private ProviderType providerType;
}
