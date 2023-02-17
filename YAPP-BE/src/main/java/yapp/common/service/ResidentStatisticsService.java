package yapp.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import yapp.common.domain.Location;
import yapp.common.domain.ResidentStatistics;
import yapp.common.repository.LocationRepository;
import yapp.common.repository.ResidentStatisticsRepository;
import yapp.domain.member.entity.YN;
import yapp.domain.town.entity.Town;
import yapp.domain.town.repository.TownRepository;

@Service
public class ResidentStatisticsService {
  private final ResidentStatisticsRepository residentStatisticsRepository;
  private final TownRepository townRepository;
  private final LocationRepository locationRepository;

  public ResidentStatisticsService(
    ResidentStatisticsRepository residentStatisticsRepository,
    TownRepository townRepository,
    LocationRepository locationRepository
  ) {
    this.residentStatisticsRepository = residentStatisticsRepository;
    this.townRepository = townRepository;
    this.locationRepository = locationRepository;
  }

  @Transactional
  public void setResidentStatisticsData() {

    List<ResidentStatistics> residentStatisticsList = this.residentStatisticsRepository.findAll();
    Map<String, ResidentStatistics> statisticsMap = residentStatisticsList
      .stream()
      .collect(
        Collectors.toMap(ResidentStatistics::getAdmNm, residentStatistics -> residentStatistics));

    List<Location> locationList = this.locationRepository
      .getLocationsByAdmNmIn(
        residentStatisticsList.stream()
          .map(ResidentStatistics::getAdmNm)
          .collect(Collectors.toList()));

    Map<Long, ResidentStatistics> residentStatisticsMap = new HashMap<>();
    locationList.forEach(location -> {
      if (statisticsMap.containsKey(location.getAdmNm())) {
        residentStatisticsMap.put(location.getObjectId(), statisticsMap.get(location.getAdmNm()));
      }
    });

    List<Town> townList = this.townRepository.findTownsByUseStatus(YN.Y);

    for (Town town : townList) {
      if (residentStatisticsMap.containsKey(town.getObjectId())) {
        ResidentStatistics residentStatistics = residentStatisticsMap.get(town.getObjectId());
        town.changeTownDescribe(createTownDescribe(residentStatistics));
      }
    }

    this.townRepository.saveAll(townList);
  }

  private String createTownDescribe(ResidentStatistics residentStatistics) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(residentStatistics.getAdmNm()).append("은 ");

    int generationDiff = residentStatistics.getTwentyGen() - residentStatistics.getThirtyGen();
    int genderDiff = residentStatistics.getFemaleNum() - residentStatistics.getMaleNum();

    stringBuilder.append(appendGenerationName(generationDiff));
    stringBuilder.append(appendGenderName(genderDiff));

    return stringBuilder.toString();
  }

  public String appendGenerationName(int generationDiff) {
    switch (calculateState(generationDiff)) {
      case 1:
        return "20대";
      case 2:
        return "30대";
      default:
        return "2030대";
    }
  }

  public String appendGenderName(int genderDiff) {
    switch (calculateState(genderDiff)) {
      case 1:
        return "여성 1인가구가 많이 살고 있어요.";
      case 2:
        return "남성 1인가구가 많이 살고 있어요.";
      default:
        return " 1인가구가 많이 살고 있어요.";
    }
  }

  public int calculateState(int diffNum) {
    return Math.abs(diffNum) <= 150 ? 3 : (diffNum > 0 ? 1 : 2);
  }
}
