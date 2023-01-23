package yapp.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.response.ApiResponse;
import yapp.common.response.ApiResponseHeader;

@Tag(name = "네트워크 연결 체크")
@RestController
public class HealthController {

  @Operation(summary = "헬스 체크")
  @GetMapping("/health")
  public ApiResponse getCheck() {
    return new ApiResponse(new ApiResponseHeader(200, "네트워크 연결 성공!!!"), null);
  }
}