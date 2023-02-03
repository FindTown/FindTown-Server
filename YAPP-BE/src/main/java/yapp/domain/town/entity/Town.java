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
@Table(name = "town")
public class Town {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @Column(name = "object_id")
  private Long objectId;

  @Column(name = "town_describe")
  private String townDescribe;

  @Column(name = "live_rank")
  private int liveRank;

  @Column(name = "cleanliness_rank")
  private String cleanlinessRank;

  @Column(name = "relief_yn")
  private String reliefYn;

  @Column(name = "life_rate")
  private int lifeRate;

  @Column(name = "crime_rate")
  private int crimeRate;

  @Column(name = "traffic_rate")
  private int trafficRate;

}