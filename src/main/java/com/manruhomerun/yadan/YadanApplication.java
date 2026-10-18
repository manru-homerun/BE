package com.manruhomerun.yadan;

import com.manruhomerun.yadan.global.properties.*;
import com.manruhomerun.yadan.auth.properties.JwtProperties;
import com.manruhomerun.yadan.auth.properties.KakaoApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({TourApiProperties.class, KboScheduleProperties.class, GlobalProperties.class,
		TourApiProperties.class,
		AiApiProperties.class,
		KakaoApiProperties.class,
		JwtProperties.class,
		S3Properties.class,
})
public class YadanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YadanApplication.class, args);
	}

}
