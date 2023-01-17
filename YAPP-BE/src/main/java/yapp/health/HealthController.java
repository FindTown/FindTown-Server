package yapp.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "네트워크 연결 체크")
@RestController
public class HealthController {

  @Operation(summary = "헬스 체크")
  @GetMapping("/health")
  public HttpMethod getCheck() {
    return HttpMethod.GET;
  }
}