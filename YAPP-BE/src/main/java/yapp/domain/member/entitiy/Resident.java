package yapp.domain.member.entitiy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resident {

  @Column(name = "resident_review")
  private String residentReview;

  @Column(name = "resident_year")
  private int residentYear;

  @Column(name = "residnet_month")
  private int residentMonth;

  public Resident(
    String residentReview,
    int residentYear,
    int residentMonth
  ) {
    this.residentReview = residentReview;
    this.residentYear = residentYear;
    this.residentMonth = residentMonth;
  }
}
