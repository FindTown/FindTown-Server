package yapp;

import java.util.Date;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
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

  @PostConstruct
  public void start() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    System.out.println("Now: " + new Date());
  }
}
