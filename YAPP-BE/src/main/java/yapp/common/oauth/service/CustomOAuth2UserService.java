package yapp.common.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import yapp.common.oauth.entity.OAuth2UserInfo;
import yapp.common.oauth.entity.OAuth2UserInfoFactory;
import yapp.common.oauth.entity.ProviderType;
import yapp.common.oauth.entity.RoleType;
import yapp.common.oauth.exception.OAuthProviderMissMatchException;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.entitiy.MemberPrincipal;
import yapp.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);

    try {
      return this.process(userRequest, user);
    } catch (Exception e) {
      e.printStackTrace();
      throw new InternalAuthenticationServiceException(e.getMessage());
    }
  }

  private OAuth2User process(
    OAuth2UserRequest userRequest,
    OAuth2User user
  ) {
    ProviderType providerType = ProviderType.valueOf(
      userRequest.getClientRegistration().getRegistrationId().toUpperCase());

    OAuth2UserInfo memberInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
      providerType, user.getAttributes());

    Member savedMember = memberRepository.findByMemberEmailAndProviderType(
      memberInfo.getEmail(), providerType);

    if (savedMember != null) {
      if (providerType != savedMember.getProviderType()) {
        throw new OAuthProviderMissMatchException(
          "요청한 " + providerType + "계정의 로그인 타입과 저장된 회원의 " + savedMember.getProviderType()
            + "계정 타입이 일치하지 않습니다."
        );
      }
      updateMember(savedMember, memberInfo);
    } else {
      savedMember = createMember(memberInfo, providerType); // 회원가입 할때 로직 수정 필요!!
    }

    return MemberPrincipal.create(savedMember, user.getAttributes());
  }

  private Member updateMember(
    Member member,
    OAuth2UserInfo memberInfo
  ) {
    if (memberInfo.getEmail() != null && !member.getEmail().equals(memberInfo.getEmail())) {
      member.setEmail(memberInfo.getEmail());
    }

    if (memberInfo.getNickname() != null && !member.getNickname()
      .equals(memberInfo.getNickname())) {
      member.setNickname(memberInfo.getNickname());
    }

    return member;
  }

  private Member createMember(
    OAuth2UserInfo memberInfo,
    ProviderType providerType
  ) {
    Member member = new Member(
      memberInfo.getMemberId(),
      memberInfo.getEmail(),
      memberInfo.getNickname(),
      providerType,
      RoleType.USER,
      null,
      null,
      null,
      null
    );

    return memberRepository.save(member);
  }
}
