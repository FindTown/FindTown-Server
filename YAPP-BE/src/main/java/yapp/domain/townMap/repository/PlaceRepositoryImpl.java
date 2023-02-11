package yapp.domain.townMap.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import yapp.domain.townMap.entity.Place;

import static yapp.domain.townMap.entity.QPlace.place;
import static yapp.domain.townMap.entity.QInfra.infra;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Place> findByInfra(Long object_id, String category, String subCategory) {
    return queryFactory.selectFrom(place)
      .innerJoin(place.infra, infra)
      .where(
        place.objectId.eq(object_id),
        infra.category.eq(category),
        infra.subCategory.eq(subCategory))
      .fetch();
  }

}
