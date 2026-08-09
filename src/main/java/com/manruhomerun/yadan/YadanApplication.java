package com.manruhomerun.yadan;

import com.manruhomerun.yadan.auth.properties.JwtProperties;
import com.manruhomerun.yadan.auth.properties.KakaoApiProperties;
import com.manruhomerun.yadan.global.properties.TourApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		TourApiProperties.class,
		KakaoApiProperties.class,
		JwtProperties.class
})
public class YadanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YadanApplication.class, args);
	}

}
