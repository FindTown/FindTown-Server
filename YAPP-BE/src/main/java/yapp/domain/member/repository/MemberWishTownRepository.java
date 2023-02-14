package yapp.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yapp.common.domain.Location;
import yapp.domain.member.entity.MemberWishTown;

public interface MemberWishTownRepository extends JpaRepository<MemberWishTown, Long> {

  List<MemberWishTown> getMemberWishTownsByMemberId(String memberId);

  Optional<MemberWishTown> getMemberWishTownByMemberIdAndLocation(
    String memberId,
    Location location
  );

  void deleteMemberWishTownsByMemberId(String memberId);
}
