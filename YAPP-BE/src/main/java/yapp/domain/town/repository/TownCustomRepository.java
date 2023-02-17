package yapp.domain.town.repository;

import java.util.List;
import yapp.domain.town.dto.TownDetailDto;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.entity.FilterStatus;
import yapp.domain.town.entity.Town;

public interface TownCustomRepository {

  List<TownDto> getTownFilterList(
    FilterStatus filterStatus,
    List<String> stationCondition
  );

  List<Town> getTownSearchList(
    String sggnm
  );

  List<TownDetailDto> getTownDetailInfo(
    Long objectId
  );

  List<Town> getMemberWishTownList(
    String memberId
  );
}
