package yapp.domain.townMap.converter;

import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yapp.common.domain.Location;
import yapp.domain.townMap.dto.response.LocationInfoResponse;

@Component
@RequiredArgsConstructor
public class LocationConverter {

  public Optional<LocationInfoResponse> toLocationInfo(
    Location location
  ) {

    String crd_str = location.getCoordinates()
      .replace(" ", "")
      .replace("[[[", "[")
      .replace("]]]", "]");

    Double[][] crd_arr = Arrays.stream(crd_str.substring(2, crd_str.length() - 2).split("\\],\\["))
      .map(e ->
        Arrays.stream(e.split("\\s*,\\s*"))
          .map(Double::parseDouble)
          .toArray(Double[]::new)
      ).toArray(Double[][]::new);

    return Optional.of(LocationInfoResponse.builder()
      .object_id(location.getObjectId())
      .adm_nm(location.getAdmNm())
      .coordinates(crd_arr)
      .build());
  }
}
