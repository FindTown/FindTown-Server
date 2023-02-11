package yapp.domain.townMap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yapp.domain.townMap.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
