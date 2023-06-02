package yapp.domain.town.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.town.entity.TownMood;

@Repository
public interface TownMoodRepository extends JpaRepository<TownMood, Long> {

  Optional<TownMood> findByTownObjectIdAndMoodKeyword(
          Long townObjectId,
          String moodKeyword
  );

  List<TownMood> findTop2ByTownObjectIdOrderByCntDesc(Long objectId);
}
