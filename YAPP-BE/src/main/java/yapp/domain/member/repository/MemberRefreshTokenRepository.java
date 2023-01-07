package yapp.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.member.entitiy.MemberRefreshToken;

@Repository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

  MemberRefreshToken findByMemberId(String memberId);

  MemberRefreshToken findByMemberIdAndRefreshToken(
    String memberId,
    String refreshToken
  );

//  MemberRefreshToken findByMemberEmailAndProviderType(
//    String memberEmail,
//    ProviderType providerType
//  );
//
//  MemberRefreshToken findByMemberEmailAndRefreshTokenAndProviderType(
//    @NotNull @Size(max = 64) String memberEmail,
//    @NotNull @Size(max = 256) String refreshToken,
//    @NotNull ProviderType providerType
//  );
}
