package com.manruhomerun.yadan;

import com.manruhomerun.yadan.baseball.properties.KboScheduleProperties;
import com.manruhomerun.yadan.global.properties.TourApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({TourApiProperties.class, KboScheduleProperties.class})
public class YadanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YadanApplication.class, args);
	}

}
