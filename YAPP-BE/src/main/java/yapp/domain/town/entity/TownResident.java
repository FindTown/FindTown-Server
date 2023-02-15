package yapp.domain.town.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yapp.common.domain.BaseEntity;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "town_resident")
public class TownResident extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @Column(name = "object_id")
  private Long objectId;

  @Column(name = "member_id")
  private String memberId;

  @Column(name = "resident_address")
  private String residentAddress;

  @Column(name = "resident_review")
  private String residentReview;

  @Column(name = "resident_year")
  private int residentYear;

  @Column(name = "resident_month")
  private int residentMonth;

  public void removeMemberId() {
    this.memberId = "none";
  }

  @Builder
  public TownResident(
    Long seq,
    Long objectId,
    String memberId,
    String residentAddress,
    String residentReview,
    int residentYear,
    int residentMonth
  ) {
    this.seq = seq;
    this.objectId = objectId;
    this.memberId = memberId;
    this.residentAddress = residentAddress;
    this.residentReview = residentReview;
    this.residentYear = residentYear;
    this.residentMonth = residentMonth;
  }

  public static TownResident EmptyResident() {
    return new TownResident(0L, 0L, "", "", "", 0, 0);
  }

}
