package yapp.domain.member.converter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yapp.common.domain.Location;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.entitiy.Member;

@Component
@RequiredArgsConstructor
public class MemberConverter {

  public Optional<MemberInfoResponse> toMemberInfo(
    Member member,
    List<Location> locationList
  ) {
    return Optional.of(MemberInfoResponse.builder()
      .memberId(member.getMemberId())
      .email(member.getEmail())
      .resident(member.getResident())
      .nickname(member.getNickname())
      .useAgreeYn(member.getUseAgreeYn())
      .privacyAgreeYn(member.getPrivacyAgreeYn())
      .providerType(member.getProviderType())
      .locationList(locationList)
      .build());
  }
}
