package com.manruhomerun.yadan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baseballOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/api").description("Base API URL"))
                .info(new Info()
                        .title("Yadan Baseball API")
                        .version("v1")
                        .description("프로야구 경기 상세 및 일정 조회를 위한 API 문서입니다.")
                        .contact(new Contact().name("Yadan Team"))
                        .license(new License().name("Internal Use")));
    }
}
