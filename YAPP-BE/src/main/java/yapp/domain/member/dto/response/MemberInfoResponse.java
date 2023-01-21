package yapp.domain.member.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import yapp.common.domain.Location;
import yapp.common.oauth.entity.ProviderType;
import yapp.domain.member.entitiy.Resident;

@Getter
@Setter
@Builder
public class MemberInfoResponse {
  private String memberId;
  private String email;
  private String nickname;
  private ProviderType providerType;
  private Resident resident;
  private String useAgreeYn;
  private String privacyAgreeYn;
  private List<Location> locationList;
}
