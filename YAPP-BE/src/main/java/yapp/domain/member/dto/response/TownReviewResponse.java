package yapp.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TownReviewResponse {
  private String residentAddress;
  private String residentReview;
  private String[] moods;
  private int residentYear;
  private int residentMonth;
}
