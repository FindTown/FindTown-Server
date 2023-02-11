package yapp.domain.town.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum InfraStatus {

  MART_CONVENIENCE_STORE("001", "마트&편의점"),
  CAFE("002", "카페"),
  POLICE_PEACE("003", "치안"),
  LIFE("004", "생활"),
  EXERCISE("005", "운동"),
  WALK("006", "산책"),
  HOSPITAL("007", "병원"),
  PHARMACY_STORE("008", "약국");
  private String code;
  private String name;

  InfraStatus(
    String code,
    String name
  ) {
    this.code = code;
    this.name = name;
  }
  
  @JsonCreator
  public static InfraStatus from(String value) {
    for (InfraStatus status : InfraStatus.values()) {
      if (status.getName().equals(value)) {
        return status;
      }
    }
    return null;
  }
}
