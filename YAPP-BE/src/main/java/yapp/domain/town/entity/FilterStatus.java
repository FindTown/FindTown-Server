package yapp.domain.town.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public enum FilterStatus {

  CONVENIENCE_FILTER("convenience", "001", "마트 & 편의점 + 생활 인프라 + 카페", convenienceFilterInfraList()),
  EXERCISE_FILTER("exercise", "002", "운동 인프라", exerciseFilterInfraList()),
  MEDICAL_FACILITY_FILTER("medical", "003", "병원&약국 인프라", medicalFilterInfraList()),
  GREENERY_FILTER("greenery", "004", "녹지", greenFilterInfraList()),
  NONE("", "005", "전체", Collections.emptyList());

  private final String value;

  @Getter
  private final String code;

  @Getter
  private final String name;

  @Getter
  private final List<InfraStatus> infraStatuses;

  @JsonCreator
  public static FilterStatus from(String value) {
    for (FilterStatus status : FilterStatus.values()) {
      if (status.getFilterValue().equals(value)) {
        return status;
      }
    }
    return null;
  }

  @JsonValue
  public String getFilterValue() {
    return this.value;
  }

  FilterStatus(
    String value,
    String code,
    String name,
    List<InfraStatus> infraStatuses
  ) {
    this.value = value;
    this.code = code;
    this.name = name;
    this.infraStatuses = infraStatuses;
  }

  private static List<InfraStatus> convenienceFilterInfraList() {
    return List.of(InfraStatus.MART_CONVENIENCE_STORE, InfraStatus.LIFE, InfraStatus.CAFE);
  }

  private static List<InfraStatus> exerciseFilterInfraList() {
    return List.of(InfraStatus.EXERCISE);
  }

  private static List<InfraStatus> medicalFilterInfraList() {
    return List.of(InfraStatus.HOSPITAL, InfraStatus.PHARMACY_STORE);
  }

  private static List<InfraStatus> greenFilterInfraList() {
    return List.of(InfraStatus.WALK);
  }

}
