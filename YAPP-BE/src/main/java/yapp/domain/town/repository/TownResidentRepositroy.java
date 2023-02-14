package yapp.domain.town.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.town.entity.TownResident;

@Repository
public interface TownResidentRepositroy extends JpaRepository<TownResident, Long> {

  Optional<TownResident> findTownResidentByMemberId(String memberId);
}
