package yapp.domain.town.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yapp.domain.town.entity.FilterStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TownFilterRequest {
  private FilterStatus filterStatus;
  private List<String> subwayList;
}
