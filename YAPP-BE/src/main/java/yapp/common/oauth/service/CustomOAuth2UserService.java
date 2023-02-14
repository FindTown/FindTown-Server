package yapp.common.oauth.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yapp.common.oauth.entity.OAuth2UserInfo;
import yapp.common.oauth.entity.OAuth2UserInfoFactory;
import yapp.common.oauth.entity.ProviderType;
import yapp.common.oauth.entity.RoleType;
import yapp.common.oauth.exception.OAuthProviderMissMatchException;
import yapp.domain.member.entity.Member;
import yapp.domain.member.entity.MemberPrincipal;
import yapp.domain.member.repository.MemberRepository;

@Slf4j
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

    Optional<Member> savedMember = memberRepository.findByEmailAndProviderType(
      memberInfo.getEmail(), providerType);

    if (savedMember.isPresent()) {
      if (providerType != savedMember.get().getProviderType()) {
        throw new OAuthProviderMissMatchException(
          "요청한 " + providerType + "계정의 로그인 타입과 저장된 회원의 " + savedMember.get().getProviderType()
            + "계정 타입이 일치하지 않습니다."
        );
      }
      updateMember(savedMember.get(), memberInfo);
    } else {
      savedMember = Optional.of(createMember(memberInfo, providerType));
    }

    return MemberPrincipal.create(savedMember.get(), user.getAttributes());
  }

  private Member updateMember(
    Member member,
    OAuth2UserInfo memberInfo
  ) {
    if (!StringUtils.hasText(memberInfo.getEmail()) && !member.getEmail()
      .equals(memberInfo.getEmail())) {
      member.setEmail(memberInfo.getEmail());
    }

    if (!StringUtils.hasText(memberInfo.getNickname()) && !member.getNickname()
      .equals(memberInfo.getNickname())) {
      member.changeNickname(memberInfo.getNickname());
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
      0
    );
    return memberRepository.save(member);
  }
}
