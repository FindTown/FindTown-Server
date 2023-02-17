package yapp;

import java.util.Date;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import yapp.common.config.AppProperties;
import yapp.common.config.CorsProperties;
import yapp.common.service.ResidentStatisticsService;

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

  @Bean
  public CommandLineRunner run(ResidentStatisticsService residentStatisticsService)
    throws Exception {
    return (String[] args) -> {
      residentStatisticsService.setResidentStatisticsData();
    };
  }
}
