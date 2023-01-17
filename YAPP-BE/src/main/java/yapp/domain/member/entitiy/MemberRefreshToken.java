package yapp.domain.member.entitiy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_refresh_token")
public class MemberRefreshToken {

  @JsonIgnore
  @Id
  @Column(name = "refresh_token_seq")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long refreshTokenSeq;

  @Column(name = "member_id", length = 64, unique = true)
  @NotNull
  @Size(max = 64)
  private String memberId;

  @Column(name = "refresh_token", length = 256)
  @NotNull
  @Size(max = 256)
  private String refreshToken;

  public MemberRefreshToken(
    @NotNull @Size(max = 64) String memberId,
    @NotNull @Size(max = 256) String refreshToken
  ) {
    this.memberId = memberId;
    this.refreshToken = refreshToken;
  }
}

