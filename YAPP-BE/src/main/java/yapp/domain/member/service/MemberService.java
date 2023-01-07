package yapp.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  public Member getMemer(String memberId) {
    return memberRepository.findByMemberId(memberId);
  }
}
