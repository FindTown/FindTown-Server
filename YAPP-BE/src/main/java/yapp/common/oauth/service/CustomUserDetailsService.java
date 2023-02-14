package yapp.common.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yapp.domain.member.entity.Member;
import yapp.domain.member.entity.MemberPrincipal;
import yapp.domain.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByMemberId(username)
      .orElseThrow(() -> new UsernameNotFoundException("등록된 회원이 아닙니다"));
    return MemberPrincipal.create(member);
  }
}
