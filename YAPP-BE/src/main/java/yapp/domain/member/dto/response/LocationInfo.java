package yapp.domain.member.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationInfo {
  private Long objectId;
  private String sidoNm;
  private String sggNm;
  private String admNm;

  public LocationInfo(
    Long objectId,
    String sidoNm,
    String sggNm,
    String admNm
  ) {
    this.objectId = objectId;
    this.sidoNm = sidoNm;
    this.sggNm = sggNm;
    this.admNm = admNm;
  }
}
