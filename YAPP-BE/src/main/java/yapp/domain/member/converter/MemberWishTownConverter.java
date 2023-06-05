package yapp.domain.member.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yapp.domain.member.dto.MemberWishTownDto;
import yapp.domain.town.entity.Town;

@Component
@RequiredArgsConstructor
public class MemberWishTownConverter {

  public MemberWishTownDto toMemberWishTownDto(
          Town town,
          String[] moods,
          String sggnm
  ) {
    return MemberWishTownDto.builder()
            .objectId(town.getObjectId())
            .moods(moods)
            .sggnm(sggnm)
            .build();
  }

}
