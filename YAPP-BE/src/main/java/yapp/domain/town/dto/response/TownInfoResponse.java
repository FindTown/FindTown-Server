package yapp.domain.town.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TownInfoResponse {
  private Long objectId;                 //행정동 id
  private String townExplanation;        //어떤 동네인가요?
  private String reliefYn;               //동네 치안 유무  ,  'Y'는 "안심보안관 활동지"
  private int lifeRate;                  //생활안전 지수
  private int crimeRate;                 //범죄 지수
  private int trafficRate;                //교통 지수
  private String cleanlinessRank;        //청결도 -> TOP10 반환 , default = 'N'
  private int liveRank;                  //살기 좋은 동네
  private int popularTownRate;           //인기 동네 순위  , default = 0
  private int popularGeneration;         //인기 세대      , default = 0
  private boolean wishTown;              //찜 여부
  private List<String> townSubwayList;   //지하철 노선 정보
  private List<String> townMoodList;     //동네 분위기 정보
  private List<String> townHotPlaceList; //근처 핫플레이스 정보
}
