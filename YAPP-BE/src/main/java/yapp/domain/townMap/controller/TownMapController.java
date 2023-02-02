package yapp.domain.townMap.controller;

import static yapp.common.config.Const.NON_USER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.response.ApiResponse;
import yapp.common.security.CurrentAuthPrincipal;
import yapp.domain.townMap.dto.response.LocationInfoResponse;
import yapp.domain.townMap.service.TownMapService;

@Slf4j
@RestController
@RequestMapping("app/townMap")
@Tag(name = "[화면]-동네지도")
public class TownMapController {

  private final TownMapService townMapService;

  public TownMapController(
    TownMapService townMapService
  ){
    this.townMapService = townMapService;
  }

  @GetMapping("/location")
  @Operation(summary = "동네 지도 좌표 조회")
  public ApiResponse getLocationInfo(
    @CurrentAuthPrincipal User memberPrincipal
  ){

    String member_id;

    try {
      member_id = memberPrincipal.getUsername();
    } catch(NullPointerException e){
      member_id = NON_USER;
    }

    LocationInfoResponse locationinfoResponse = this.townMapService.getLocationInfo(
      member_id);

    return ApiResponse.success("location-info", locationinfoResponse);
  }

}
