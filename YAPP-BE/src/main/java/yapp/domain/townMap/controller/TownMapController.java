package yapp.domain.townMap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.response.ApiResponse;
import yapp.common.security.CurrentAuthPrincipal;
import yapp.domain.townMap.dto.response.LocationInfoResponse;
import yapp.domain.townMap.service.TownMapService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("app/townMap")
@Tag(name = "[화면]-동네지도", description = "동네지도 API")
public class TownMapController {

  private final TownMapService townMapService;

  @GetMapping("/location")
  @Operation(summary = "동네 지도 좌표 조회")
  public ApiResponse getLocationInfo(
    @CurrentAuthPrincipal User memberPrincipal,
    @RequestParam(value = "id", required = false) Optional<String> objectId
  ){

    LocationInfoResponse locationinfoResponse = this.townMapService.getLocationInfo(
      (
        memberPrincipal == null ? Optional.empty()
          : Optional.ofNullable(memberPrincipal.getUsername())), objectId);

    return ApiResponse.success("location-info", locationinfoResponse);
  }

  @GetMapping("/location/{object_id}/infra/{category}")
  @Operation(summary = "동네 인프라 장소 조회")
  public ApiResponse getInfraPlaceInfo(
    @PathVariable Long object_id,
    @PathVariable String category
  ){

    return ApiResponse.success(townMapService.getInfraPlaceInfo(object_id, category));
  }

  @GetMapping("/location/{object_id}/theme/{category}")
  @Operation(summary = "동네 테마지도 장소 조회")
  public ApiResponse getThemePlaceInfo(
    @PathVariable Long object_id,
    @PathVariable String category
  ){

    return ApiResponse.success(townMapService.getThemePlaceInfo(object_id, category));
  }

}
