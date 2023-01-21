package yapp.domain.member.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import yapp.domain.member.entitiy.MemberWishTown;

public interface MemberWishTownRepository extends JpaRepository<MemberWishTown, Long> {

  List<MemberWishTown> getMemberWishTownsByMemberId(String memberId);
}
