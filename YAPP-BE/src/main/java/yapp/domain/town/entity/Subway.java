package yapp.domain.town.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "subway")
public class Subway {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @Column(name = "station_cd", columnDefinition = "VARCHAR(50)")
  private String stationCd;

  @Column(name = "station_nm", columnDefinition = "VARCHAR(100)")
  private String stationNm;

  @Column(name = "station_nm_eng", columnDefinition = "VARCHAR(100)")
  private String stationNmEng;

  @Column(name = "line_num", columnDefinition = "VARCHAR(50)")
  private String lineNum;

}