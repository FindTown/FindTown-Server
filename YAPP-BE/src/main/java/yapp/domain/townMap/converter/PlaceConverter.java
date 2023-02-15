package yapp.domain.townMap.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import yapp.domain.townMap.dto.ThemePlaceDto;
import yapp.domain.townMap.dto.response.ThemePlaceResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceConverter {

  public ThemePlaceResponse toThemePlace(
    ThemePlaceDto themePlaceDto
  ) {

    String[] placeInfo = getPlaceInfo(themePlaceDto.getName());

    return ThemePlaceResponse.builder()
        .name(placeInfo[0])
        .address(themePlaceDto.getAddress())
        .x(themePlaceDto.getX())
        .y(themePlaceDto.getY())
        .subCategory((themePlaceDto.getTheme().getSubCategoryName()))
        .foodCategory(placeInfo[1])
      .build();
  }

  private String[] getPlaceInfo (String str) {
    Pattern pattern = Pattern.compile("\\[(.*?)\\]");
    Matcher matcher = pattern.matcher(str);
    if(matcher.find()){
      return new String[] { str.split("\\[")[0], matcher.group(1).trim() };
    }
    return new String[] { str, null };
  }

}
