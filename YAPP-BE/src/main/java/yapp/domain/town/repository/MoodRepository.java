package yapp.domain.town.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.town.entity.Mood;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
  Optional<Mood> findByKeyword(String keyword);
}
