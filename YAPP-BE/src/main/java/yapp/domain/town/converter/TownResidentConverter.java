package yapp.domain.town.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.town.entity.TownResident;

@Component
@RequiredArgsConstructor
public class TownResidentConverter {

  public TownResident toEntity(
          MemberSignUpRequest memberSignUpRequest,
          Long objectId
  ) {
    return TownResident.builder()
            .objectId(objectId)
            .memberId(memberSignUpRequest.getMemberId())
            .residentAddress(memberSignUpRequest.getResident().getResidentAddress())
            .residentReview(memberSignUpRequest.getResident().getResidentReview())
            .moods(String.join(",", memberSignUpRequest.getResident().getMoods()))
            .residentYear(memberSignUpRequest.getResident().getResidentYear())
            .residentMonth(memberSignUpRequest.getResident().getResidentMonth())
            .build();
  }
}
