package yapp.domain.member.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import yapp.common.oauth.entity.ProviderType;
import yapp.domain.member.entity.Resident;

@Getter
@Setter
@Builder
public class MemberInfoResponse {
  private String memberId;
  private String email;
  private String nickname;
  private ProviderType providerType;
  private List<Resident> resident;
  private boolean useAgreeYn;
  private boolean privacyAgreeYn;
  private List<LocationInfo> locationList;
}

