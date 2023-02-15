package yapp.domain.townMap.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import yapp.domain.townMap.dto.QInfraPlaceDto;
import yapp.domain.townMap.dto.QThemePlaceDto;
import yapp.domain.townMap.dto.InfraPlaceDto;
import yapp.domain.townMap.dto.ThemePlaceDto;

import static yapp.domain.townMap.entity.QPlace.place;
import static yapp.domain.townMap.entity.QInfra.infra;
import static yapp.domain.townMap.entity.QTheme.theme;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<InfraPlaceDto> findByInfra(Long object_id, String category) {
    return queryFactory
      .select(
        new QInfraPlaceDto(
          place.name,
          place.address,
          place.x,
          place.y,
          infra
        )
      )
      .from(place)
      .innerJoin(place.infra, infra)
      .where(
        place.objectId.eq(object_id),
        infra.category.eq(category))
      .fetch();
  }

  @Override
  public List<ThemePlaceDto> findByTheme(Long object_id, String category) {
    return queryFactory
      .select(
        new QThemePlaceDto(
          place.name,
          place.address,
          place.x,
          place.y,
          theme
        )
      )
      .from(place)
      .innerJoin(place.theme, theme)
      .where(
        place.objectId.eq(object_id),
        theme.category.eq(category))
      .fetch();
  }

}
