package yapp.domain.town.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yapp.domain.member.entitiy.YN;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "town")
public class Town implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @Enumerated(EnumType.STRING)
  @Column(name = "use_status")
  private YN useStatus;

  @Column(name = "object_id")
  private Long objectId;

  @Column(name = "town_introduction")
  private String townIntroduction;

  @Column(name = "town_describe")
  private String townDescribe;

  @Column(name = "live_rank", columnDefinition = "TINYINT")
  private int liveRank;

  @Column(name = "cleanliness_rank")
  private String cleanlinessRank;

  @Column(name = "relief_yn")
  private String reliefYn;

  @Column(name = "life_rate", columnDefinition = "TINYINT")
  private int lifeRate;

  @Column(name = "crime_rate", columnDefinition = "TINYINT")
  private int crimeRate;

  @Column(name = "traffic_rate", columnDefinition = "TINYINT")
  private int trafficRate;

}