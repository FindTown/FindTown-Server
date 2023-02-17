package yapp.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberWishTownDto {
  private Long objectId;
  private String townExplanation;

  public MemberWishTownDto (
    Long objectId,
    String townExplanation
  ) {
    this.objectId = objectId;
    this.townExplanation = townExplanation;
  }
}
