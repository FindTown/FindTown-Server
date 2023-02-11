package yapp.domain.town.comparator;

import java.util.Comparator;
import org.springframework.stereotype.Component;
import yapp.domain.town.dto.TownDto;

@Component
public class TownComparator {

  private static final String UNIVERSITY_HOSPITAL = "015";

  public static final Comparator<TownDto> compareByInfraCount = Comparator.comparingInt(
    townData -> {
      return townData.getPlaceSet().size();
    });

  public static final Comparator<TownDto> compareBySubwayCount = Comparator.comparingInt(
    townData -> {
      return townData.getTownSubwaySet().size();
    });

  public static final Comparator<TownDto> compareByUniversityHospital = Comparator.comparingLong(
    townData -> {
      return townData.getPlaceSet()
        .stream()
        .filter(place -> place.getInfra().getSubCategory().equals(UNIVERSITY_HOSPITAL)).count();
    });

  public static final Comparator<TownDto> compareByMedical = Comparator.comparingLong(
    townData -> {
      return townData.getPlaceSet()
        .stream()
        .filter(place -> !place.getInfra().getSubCategory().equals(UNIVERSITY_HOSPITAL)).count();
    });

  public static final Comparator<TownDto> compareMedicalFilter =
    compareByUniversityHospital.thenComparing(compareByMedical).reversed();

  public static final Comparator<TownDto> compareTownFilter =
    compareBySubwayCount.thenComparing(compareBySubwayCount).reversed();
}
