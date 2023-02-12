package yapp.domain.town.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static yapp.common.domain.QLocation.location;
import static yapp.domain.member.entitiy.YN.Y;
import static yapp.domain.town.entity.QMood.mood;
import static yapp.domain.town.entity.QSubway.subway;
import static yapp.domain.town.entity.QTown.town;
import static yapp.domain.town.entity.QTownHotPlace.townHotPlace;
import static yapp.domain.town.entity.QTownMood.townMood;
import static yapp.domain.town.entity.QTownPopular.townPopular;
import static yapp.domain.town.entity.QTownSubway.townSubway;
import static yapp.domain.townMap.entity.QInfra.infra;
import static yapp.domain.townMap.entity.QPlace.place;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import yapp.domain.town.dto.QTownDto;
import yapp.domain.town.dto.TownDetailDto;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.entity.FilterStatus;
import yapp.domain.town.entity.InfraStatus;
import yapp.domain.town.entity.Mood;
import yapp.domain.town.entity.Subway;
import yapp.domain.town.entity.Town;
import yapp.domain.town.entity.TownHotPlace;

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
      .on(townSubway.subway.id.eq(subway.id), subwayContain(stationCondition))
      .innerJoin(place)
      .on(town.objectId.eq(place.objectId))
      .innerJoin(infra)
      .on(place.infra.id.eq(infra.id), eqInfraType(filterStatus))
      .where(town.useStatus.eq(Y))
      .fetch();
  }

  @Override
  public List<Town> getTownSearchList(
    String sggNm
  ) {
    return jpaQueryFactory
      .selectFrom(town)
      .innerJoin(location)
      .on(town.objectId.eq(location.objectId))
      .where(location.sggNm.eq(sggNm), town.useStatus.eq(Y))
      .fetch();
  }

  @Override
  public List<TownDetailDto> getTownDetailInfo(Long objectId) {
    return jpaQueryFactory
      .from(town)
      .innerJoin(townSubway)
      .on(town.objectId.eq(townSubway.town.objectId))
      .innerJoin(subway)
      .on(townSubway.subway.id.eq(subway.id))
      .innerJoin(townMood)
      .on(town.objectId.eq(townMood.town.objectId))
      .innerJoin(mood)
      .on(townMood.mood.eq(mood))
      .innerJoin(townPopular)
      .on(town.objectId.eq(townPopular.objectId))
      .innerJoin(townHotPlace)
      .on(town.objectId.eq(townHotPlace.objectId))
      .where(town.objectId.eq(objectId), town.useStatus.eq(Y))
      .transform(
        groupBy(town.objectId).list(
          Projections.fields(
            TownDetailDto.class,
            town.objectId,
            town.townIntroduction,
            town.reliefYn,
            list(
              Projections.fields(
                Subway.class,
                subway.id,
                subway.lineNum
              )
            ).as("townSubwayList"),
            list(
              Projections.fields(
                Mood.class,
                mood.id,
                mood.categoryId,
                mood.categoryNm,
                mood.keyword
              )
            ).as("townMoodList"),
            townPopular,
            list(
              Projections.fields(
                TownHotPlace.class,
                townHotPlace.seq,
                townHotPlace.objectId,
                townHotPlace.hotPlaceNm
              )
            ).as("townHotPlaceList"),
            town.lifeRate,
            town.crimeRate,
            town.trafficRate,
            town.liveRank,
            town.cleanlinessRank
          )
        )
      );
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