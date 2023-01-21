package yapp.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "location")
public class Location {

  @Id
  @Column(name = "object_id")
  private Long objectId;

  @Column(name = "adm_nm")
  private String admNm;

  @Column(name = "adm_cd")
  private String admCd;

  @Column(name = "adm_cd2")
  private String admCd2;

  @Column(name = "sido")
  private String sido;

  @Column(name = "sidonm")
  private String sidoNm;

  @Column(name = "sgg")
  private String sgg;

  @Column(name = "sggnm")
  private String sggNm;

  @Column(name = "adm_cd8")
  private String admCd8;
}
