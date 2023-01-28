package yapp.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import java.util.Arrays;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenAPIConfig {

  //  @Value("${ip.town-scoop}")
//  private String SEVER_IP;
  private static final String PROD = "prod";
  private static final String LOCAL = "local";
  private static final String EXCEPT = "except";
  private final Environment environment;

  public OpenAPIConfig(
    Environment environment
  ) {
    this.environment = environment;
  }

  @Bean
  public OpenApiCustomiser customOpenAPI(BuildProperties buildProperties) {
    return openAPI -> {
      if (Arrays.asList(environment.getActiveProfiles()).contains(PROD)) {
        openAPI.info(
            new Info()
              .title("TownScoop API")
              .version(buildProperties.getVersion())
              .termsOfService("https://townscoop.site/")
              .license(
                new License().name("Apache 2.0").url("http://springdoc.org")
              )
          )
          .components(
            openAPI.getComponents().addSecuritySchemes(
              "bearer",
              new SecurityScheme()
                .type(Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
            )
          )
          .addSecurityItem(
            new SecurityRequirement().addList(
              "bearer", Arrays.asList("read", "write")
            )
          );
      } else if (Arrays.asList(environment.getActiveProfiles()).contains(LOCAL)) {
        openAPI.info(
            new Info()
              .title("TownScoop API")
              .version(buildProperties.getVersion())
              .termsOfService("http://localhost:8080/")
              .license(
                new License().name("Apache 2.0").url("http://springdoc.org")
              )
          )
          .components(
            openAPI.getComponents().addSecuritySchemes(
              "bearer",
              new SecurityScheme()
                .type(Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
            )
          )
          .addSecurityItem(
            new SecurityRequirement().addList(
              "bearer", Arrays.asList("read", "write")
            )
          );
      } else {
        openAPI.setComponents(new Components());
        openAPI.setPaths(new Paths());
      }
    };
  }
}
