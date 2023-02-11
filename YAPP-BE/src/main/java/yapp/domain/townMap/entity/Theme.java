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
@Table(name = "theme")
public class Theme {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column (name = "category")
  private String category;

  @NotNull
  @Column (name = "sub_category")
  private String subCategory;

  @NotNull
  @Column (name = "category_name")
  private String categoryName;

  @NotNull
  @Column (name = "sub_category_name")
  private String subCategoryName;

  @Builder
  public Theme(
    Long id,
    String category,
    String subCategory,
    String categoryName,
    String subCategoryName
  ) {
    this.id = id;
    this.category = category;
    this.subCategory = subCategory;
    this.categoryName = categoryName;
    this.subCategoryName = subCategoryName;
  }

}
