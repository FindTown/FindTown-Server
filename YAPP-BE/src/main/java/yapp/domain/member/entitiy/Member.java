package yapp.domain.member.entitiy;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yapp.common.config.Const;
import yapp.common.domain.BaseEntity;
import yapp.common.oauth.entity.ProviderType;
import yapp.common.oauth.entity.RoleType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq", columnDefinition = "BIGINT")
  private Long seq;

  @Column(name = "member_id")
  @NotNull
  private String memberId;

  @Pattern(regexp = "\\b[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}\\b")
  @Column(name = "email")
  private String email;

  @Column(name = "nickname")
  private String nickname; // 중복 확인 로직 필요

  @NotNull
  @Size(max = 128)
  @Column(name = "password")
  private String password;

  @Column(name = "provider_type", length = 20)
  @Enumerated(EnumType.STRING)
  @NotNull
  private ProviderType providerType; //이 값으로 소셜 로그인간 이메일 구분한다.

  @Column(name = "role_type", length = 20)
  @Enumerated(EnumType.STRING)
  @NotNull
  private RoleType roleType;

  @Column(name = "interest_town_id")
  private String interestTownId; //objec_id 의미

  @Embedded
  private Resident resident;

  @Column(name = "use_agree_yn") //이용약관 동의1
  private String useAgreeYn;

  @Column(name = "privacy_agree_yn") //이용약관 동의2
  private String privacyAgreeYn;

  public void setEmail(String email) {
    this.email = email;
  }

  public void setProviderType(ProviderType providerType) {
    this.providerType = providerType;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Member(
    String memberId,
    String email,
    String nickname,
    ProviderType providerType,
    RoleType roleType,
    String interestTownId,
    Resident resident,
    String useAgreeYn,
    String privacyAgreeYn
  ) {
    this.memberId = memberId;
    this.email = email;
    this.nickname = nickname;
    this.password = Const.DEFAULT_PASSWORD;
    this.providerType = providerType;
    this.roleType = roleType;
    this.interestTownId = interestTownId;
    this.resident = resident;
    this.useAgreeYn = useAgreeYn;
    this.privacyAgreeYn = privacyAgreeYn;
  }
}

