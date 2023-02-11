package yapp.domain.town.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.response.ApiResponse;
import yapp.common.security.CurrentAuthPrincipal;
import yapp.domain.town.dto.request.TownFilterRequest;
import yapp.domain.town.dto.response.TownFilterResponse;
import yapp.domain.town.service.TownService;

@RestController
@RequestMapping("/app/town")
public class TownController {

  private final TownService townService;

  public TownController(TownService townService) {
    this.townService = townService;
  }

  // 동네 소개 조회 API
  @GetMapping("/info")
  public ApiResponse getTownInfo() {

    return ApiResponse.success("", null);
  }

  @PostMapping("/filter")
  @Operation(summary = "동네 필터 검색")
  @Tag(name = "[화면]-동네 찾기")
  public ApiResponse getTownFilter(
    @CurrentAuthPrincipal User memberPrincipal,
    @RequestBody TownFilterRequest townFilterRequest
  ) {

    System.out.println("townFilter Request : " + townFilterRequest.getFilterStatus() + "  "
      + townFilterRequest.getSubwayList());

    List<TownFilterResponse> townFilterResponse = townService.getTownFilter((
      memberPrincipal == null ? Optional.empty()
        : Optional.ofNullable(memberPrincipal.getUsername())), townFilterRequest);
    return ApiResponse.success("town_filter", townFilterResponse);
  }

}
