package yapp.domain.town.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.town.entity.TownResident;

@Repository
public interface TownResidentRepositroy extends JpaRepository<TownResident, Long> {

  List<TownResident> findTownResidentByMemberId(String memberId);
}
