package yapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import yapp.common.config.AppProperties;
import yapp.common.config.CorsProperties;

@SpringBootApplication
@EnableConfigurationProperties({ CorsProperties.class, AppProperties.class })
public class YappBeApplication {
  public static void main(String[] args) {
    SpringApplication.run(YappBeApplication.class, args);
  }
}
