package yapp.domain.townMap.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place")
public class Place {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="infra_id")
  private Infra infra;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="theme_id")
  private Theme theme;

  @NotNull
  @Column (name = "name")
  private String name;

  @NotNull
  @Column (name = "address")
  private String address;

  @Column(name="x")
  private Long x;

  @Column(name="y")
  private Long y;

  @Column(name="object_id")
  private Long objectId;

  @Builder
  public Place(Long id, Infra infra, Theme theme, String name, String address, Long x, Long y, Long objectId){
    this.id = id;
    this.infra = infra;
    this.theme = theme;
    this.name = name;
    this.address = address;
    this.x = x;
    this.y = y;
    this.objectId = objectId;
  }

}
