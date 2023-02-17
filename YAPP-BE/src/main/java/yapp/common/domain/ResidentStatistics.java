package yapp.common.domain;

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
@Table(name = "resident_statistics")
public class ResidentStatistics {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @Column(name = "adm_nm")
  private String admNm;

  @Column(name = "female_num")
  private int femaleNum;

  @Column(name = "male_num")
  private int maleNum;

  @Column(name = "twenty_generation")
  private int twentyGen;

  @Column(name = "thirty_generation")
  private int thirtyGen;
}
