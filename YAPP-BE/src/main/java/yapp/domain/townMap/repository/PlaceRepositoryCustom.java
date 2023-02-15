package yapp.domain.townMap.repository;

import java.util.List;
import yapp.domain.townMap.dto.InfraPlaceDto;
import yapp.domain.townMap.dto.ThemePlaceDto;

public interface PlaceRepositoryCustom {
  List<InfraPlaceDto> findByInfra(Long object_id, String category);
  List<ThemePlaceDto> findByTheme(Long object_id, String category);
}
