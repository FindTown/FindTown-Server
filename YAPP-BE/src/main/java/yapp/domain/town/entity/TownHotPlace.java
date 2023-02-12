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
@Table(name = "town_hotplace")
public class TownHotPlace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @Column(name = "object_id")
  private Long objectId;

  @Column(name = "hotplace_nm")
  private String hotPlaceNm;
}
