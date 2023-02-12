package yapp.domain.town.repository;

import java.util.List;
import yapp.domain.town.dto.TownDetailDto;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.entity.FilterStatus;

public interface TownCustomRepository {

  List<TownDto> getTownFilterList(
    FilterStatus filterStatus,
    List<String> stationCondition
  );

  List<TownDetailDto> getTownDetailInfo(
    Long objectId
  );
}
