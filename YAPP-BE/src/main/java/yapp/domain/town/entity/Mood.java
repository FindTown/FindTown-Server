package yapp.domain.town.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mood")
public class Mood {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;

  @Column(name = "category_id", columnDefinition = "VARCHAR(50)")
  @NotNull
  private String categoryId;

  @Column(name = "category_nm", columnDefinition = "VARCHAR(50)")
  @NotNull
  private String categoryNm;

  @Column(name = "keyword", columnDefinition = "VARCHAR(50)")
  @NotNull
  private String keyword;
}
