package yapp.domain.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resident {

  private String residentAddress;

  private String residentReview;
  private String[] moods;

  private int residentYear;

  private int residentMonth;

  public Resident(
          String residentAddress,
          String residentReview,
          String[] moods,
          int residentYear,
          int residentMonth
  ) {
    this.residentAddress = residentAddress;
    this.residentReview = residentReview;
    this.moods = moods;
    this.residentYear = residentYear;
    this.residentMonth = residentMonth;
  }
}
