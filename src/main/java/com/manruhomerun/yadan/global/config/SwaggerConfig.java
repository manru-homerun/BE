package com.manruhomerun.yadan.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI baseballOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/api").description("Base API URL"))
                .components(new Components()
                        .addSecuritySchemes(
                                BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("서비스에서 발급한 Access Token을 입력합니다.")
                        ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .info(new Info()
                        .title("야단법석")
                        .version("v1")
                        .description("관광데이터 활용공모전 참가를 위한 야구 경기 기반 여행 코스 추천 서비스 \"야단법석\" API 문서")
                        .contact(new Contact().name("Team 만루홈런"))
                        .license(new License().name("Internal Use")));
    }
}
