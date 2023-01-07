package yapp.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.common.oauth.entity.ProviderType;
import yapp.domain.member.entitiy.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
  Member findByMemberEmailAndProviderType(
    String memberEmail,
    ProviderType providerType
  );

  Member findByMemberId(String memberId);
}
