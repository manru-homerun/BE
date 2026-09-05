package com.manruhomerun.yadan.auth.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "env.jwt")
public class JwtProperties {

    private final String accessSecret;
    private final String refreshSecret;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;
}
