package yapp.domain.town.repository;

import static yapp.domain.member.entitiy.YN.Y;
import static yapp.domain.town.entity.QSubway.subway;
import static yapp.domain.town.entity.QTown.town;
import static yapp.domain.town.entity.QTownSubway.townSubway;
import static yapp.domain.townMap.entity.QInfra.infra;
import static yapp.domain.townMap.entity.QPlace.place;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import yapp.domain.town.dto.QTownDto;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.entity.FilterStatus;
import yapp.domain.town.entity.InfraStatus;

@Repository
public class TownCustomRepositoryImpl implements
  TownCustomRepository {

  public JPAQueryFactory jpaQueryFactory;

  public TownCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<TownDto> getTownFilterList(
    FilterStatus filterStatus,
    List<String> stationCondition
  ) {
    return jpaQueryFactory
      .select(
        new QTownDto(
          town.objectId,
          place,
          townSubway,
          town.reliefYn,
          town.lifeRate,
          town.crimeRate,
          town.trafficRate,
          town.townIntroduction
        )
      )
      .from(town)
      .innerJoin(townSubway)
      .on(town.objectId.eq(townSubway.town.objectId))
      .innerJoin(subway)
      .on(townSubway.subway.stationCd.eq(subway.stationCd), subwayContain(stationCondition))
      .innerJoin(place)
      .on(town.objectId.eq(place.objectId))
      .innerJoin(infra)
      .on(place.infra.id.eq(infra.id), eqInfraType(filterStatus))
      .where(town.useStatus.eq(Y))
      .fetch();
  }

  private BooleanBuilder subwayContain(List<String> stationCondition) {
    BooleanBuilder builder = new BooleanBuilder();
    stationCondition.forEach(lineNum -> {
      builder.or(
        subway.lineNum.eq(lineNum)
      );
    });
    return builder;
  }

  private BooleanExpression eqInfraType(FilterStatus filterStatus) {
    Set<String> infraStatuses = filterStatus.getInfraStatuses()
      .stream().map(InfraStatus::getCode).collect(Collectors.toSet());

    return infra.subCategory.in(infraStatuses);
  }

}