package yapp.common.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.entitiy.MemberPrincipal;
import yapp.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByMemberId(username);
    if (member == null) {
      throw new UsernameNotFoundException("회원 ID로 계정을 조회할 수 없다.");
    }
    return MemberPrincipal.create(member);
  }
}
