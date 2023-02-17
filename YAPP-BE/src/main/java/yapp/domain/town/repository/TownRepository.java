package yapp.domain.town.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.member.entity.YN;
import yapp.domain.town.entity.Town;

@Repository
public interface TownRepository extends JpaRepository<Town, Long> {

  List<Town> findTownsByUseStatus(YN y);
}
