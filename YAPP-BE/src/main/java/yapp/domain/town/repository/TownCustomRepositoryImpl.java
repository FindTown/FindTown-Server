package yapp.domain.town.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static yapp.common.domain.QLocation.location;
import static yapp.domain.member.entity.WishStatus.YES;
import static yapp.domain.member.entity.YN.Y;
import static yapp.domain.town.entity.QMood.mood;
import static yapp.domain.town.entity.QSubway.subway;
import static yapp.domain.town.entity.QTown.town;
import static yapp.domain.town.entity.QTownHotPlace.townHotPlace;
import static yapp.domain.town.entity.QTownMood.townMood;
import static yapp.domain.town.entity.QTownPopular.townPopular;
import static yapp.domain.town.entity.QTownSubway.townSubway;
import static yapp.domain.townMap.entity.QInfra.infra;
import static yapp.domain.townMap.entity.QPlace.place;
import static yapp.domain.member.entity.QMemberWishTown.memberWishTown;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;
import yapp.domain.member.entity.WishStatus;
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
      .leftJoin(subway)
      .on(townSubway.subway.id.eq(subway.id))
      .leftJoin(place)
      .on(town.objectId.eq(place.objectId))
      .leftJoin(infra)
      .on(place.infra.id.eq(infra.id))
      .where(town.useStatus.eq(Y), subwayContain(stationCondition), eqInfraType(filterStatus))
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
      .leftJoin(townSubway)
      .on(town.objectId.eq(townSubway.town.objectId))
      .leftJoin(subway)
      .on(townSubway.subway.id.eq(subway.id))
      .leftJoin(townMood)
      .on(town.objectId.eq(townMood.town.objectId))
      .leftJoin(mood)
      .on(townMood.mood.eq(mood))
      .leftJoin(townPopular)
      .on(town.objectId.eq(townPopular.objectId))
      .leftJoin(townHotPlace)
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

  @Override
  public List<Town> getMemberWishTownList(
    String memberId
  ) {
    return jpaQueryFactory
      .selectFrom(town)
      .innerJoin(memberWishTown)
      .on(town.objectId.eq(memberWishTown.location.objectId))
      .where(memberWishTown.memberId.eq(memberId), memberWishTown.wishStatus.eq(YES))
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

  private BooleanBuilder eqInfraType(FilterStatus filterStatus) {
    List<InfraStatus> infraStaticCondition = filterStatus.getInfraStatuses();
    BooleanBuilder builder = new BooleanBuilder();
    infraStaticCondition.forEach(infraStatus -> {
      builder.or(
        infra.subCategory.eq(infraStatus.getCode())
      );
    });
    return builder;
  }

}