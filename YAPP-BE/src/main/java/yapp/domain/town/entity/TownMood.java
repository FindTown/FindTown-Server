package yapp.domain.town.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "town_mood")
public class TownMood {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "town_object_id", referencedColumnName = "object_id", nullable = false)
  private Town town;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mood_id", referencedColumnName = "id", nullable = false)
  private Mood mood;

  @Column(name = "cnt", columnDefinition = "BIGINT")
  private Long cnt;

  @Version
  private int version;

  public TownMood(
          Town town,
          Mood mood,
          Long cnt
  ) {
    this.town = town;
    this.mood = mood;
    this.cnt = cnt;
  }

  public void changeMoodCnt(Long cnt) {
    this.cnt = cnt;
  }
}