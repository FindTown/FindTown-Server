package yapp.domain.member.converter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import yapp.common.config.Const;
import yapp.common.domain.Location;
import yapp.common.oauth.entity.RoleType;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.entitiy.Member;

@Component
@RequiredArgsConstructor
public class MemberConverter {

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

  public Member toEntity(
    MemberSignUpRequest memberSignUpRequest
  ) {
    Member member = Member.builder()
      .memberId(memberSignUpRequest.getMemberId())
      .email(memberSignUpRequest.getEmail())
      .nickname(memberSignUpRequest.getNickname())
      .providerType(memberSignUpRequest.getProviderType())
      .resident(memberSignUpRequest.getResident())
      .useAgreeYn(memberSignUpRequest.getUseAgreeYn())
      .privacyAgreeYn(memberSignUpRequest.getPrivacyAgreeYn())
      .useStatus(Const.USE_MEMBERS)
      .roleType(RoleType.USER)
      .build();
    member.encodeDefaultPassword(passwordEncoder);
    return member;
  }
}
