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
@Table(name = "infra")
public class Infra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "category")
  private String category;

  @Column(name = "sub_category")
  private String subCategory;

  @Column(name = "category_name")
  private String categoryName;

  @Column(name = "sub_category_name")
  private String subCategoryName;

}
