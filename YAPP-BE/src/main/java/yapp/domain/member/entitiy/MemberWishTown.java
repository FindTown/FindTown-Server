package yapp.domain.member.entitiy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import yapp.common.domain.BaseEntity;
import yapp.common.domain.Location;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_wish_town")
@DynamicUpdate
public class MemberWishTown extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "member_id")
  private String memberId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "location_object_id", referencedColumnName = "object_id", nullable = false)
  private Location location;

  @Enumerated(EnumType.STRING)
  @Column(name = "wish_status", columnDefinition = "VARCHAR(50)")
  private WishStatus wishStatus;

  @Builder
  public MemberWishTown(
    String memberId,
    Location location,
    WishStatus wishStatus
  ) {
    this.memberId = memberId;
    this.location = location;
    this.wishStatus = wishStatus;
  }

  public void changeWishStatus(WishStatus status) {
    this.wishStatus = status;
  }
}
