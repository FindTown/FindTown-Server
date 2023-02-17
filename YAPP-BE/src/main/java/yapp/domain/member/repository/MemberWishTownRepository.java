package yapp.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yapp.common.domain.Location;
import yapp.domain.member.entity.MemberWishTown;
import yapp.domain.member.entity.WishStatus;

public interface MemberWishTownRepository extends JpaRepository<MemberWishTown, Long> {

  List<MemberWishTown> getMemberWishTownsByMemberId(String memberId);

  List<MemberWishTown> getMemberWishTownsByMemberIdAAndWishStatus(
    String memberId,
    WishStatus wishStatus
  );

  Optional<MemberWishTown> getMemberWishTownByMemberIdAndLocation(
    String memberId,
    Location location
  );

  Optional<MemberWishTown> getMemberWishTownByMemberIdAndLocationAAndWishStatus(
    String memberId,
    Location location,
    WishStatus wishStatus
  );

  void deleteMemberWishTownsByMemberId(String memberId);
}
