package yapp.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.common.oauth.entity.ProviderType;
import yapp.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsAllByNickname(String nickname);

  Optional<Member> findByEmailAndProviderType(
    String memberEmail,
    ProviderType providerType
  );

  Optional<Member> findByMemberId(String memberId);

  Optional<Member> findByMemberIdAndUseStatus(
    String memberId,
    int useStatus
  );
}
