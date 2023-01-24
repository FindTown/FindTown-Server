package yapp.domain.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberSignInRequest {
  String memberId;
}
