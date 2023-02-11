package yapp.domain.townMap.repository;

import java.util.List;
import yapp.domain.townMap.entity.Place;

public interface PlaceRepositoryCustom {
  List<Place> findByInfra(Long object_id, String category, String sub_category);
}
