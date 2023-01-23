package yapp.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.member.entitiy.MemberRefreshToken;

@Repository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

  void deleteByMemberId(String memberId);

  MemberRefreshToken findByMemberId(String memberId);

  Optional<MemberRefreshToken> findByMemberIdAndRefreshToken(
    String memberId,
    String refreshToken
  );
}
