package yapp.common.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
  private String allowedOrigins;
  private String allowedMethods;
  private String allowedHeaders;
  private Long maxAge;
}
