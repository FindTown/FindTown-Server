package yapp.common.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.common.domain.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

  Optional<Location> getLocationByObjectId(Long objectId);
}
