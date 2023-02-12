package yapp.domain.town.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TownInfoResponse {
  private Long objectId;              //행정동 id
  private String townExplanation;     //어떤 동네인가요?
  private String reliefYn;            //치안 : reliefYn -> 동네 치안 유무 Y이면 "안심보안관 활동지"
  private int lifeRate;               //생활안전 지수
  private int crimeRate;              //범죄 지수
  private int trafficRate;             //교통 지수
  private String cleanlinessRank;     //청결도 -> TOP10 반환
  private int liveRank;            //살기 좋은 동네
  private int popularTownRate;        //인기 동네 순위
  private int popularGeneration;            //인기 세대
  private boolean wishTown;              //찜 여부
  private List<String> townSubwayList;   //지하철 노선 정보 반환
  private List<String> townMoodList;
  private List<String> townHotPlaceList;
}
