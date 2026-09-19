package com.manruhomerun.yadan.auth.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manruhomerun.yadan.auth.dto.kakao.KakaoTokenInfoResponse;
import com.manruhomerun.yadan.auth.dto.kakao.KakaoUnlinkResponse;
import com.manruhomerun.yadan.auth.dto.kakao.KakaoUserInfoResponse;
import com.manruhomerun.yadan.auth.error.AuthErrorCode;
import com.manruhomerun.yadan.auth.error.exception.AuthException;
import com.manruhomerun.yadan.auth.properties.KakaoApiProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private static final String TOKEN_INFO_PATH = "/v1/user/access_token_info";
    private static final String USER_INFO_PATH = "/v2/user/me";
    private static final String UNLINK_PATH = "/v1/user/unlink";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final KakaoApiProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    // 카카오 엑세스 토큰 조회 (토큰 검증)
    public KakaoTokenInfoResponse getTokenInfo(String accessToken) {
        return get(TOKEN_INFO_PATH, accessToken, KakaoTokenInfoResponse.class);
    }

    // 카카오 엑세스 토큰에 해당하는 사용자 조회
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        return get(USER_INFO_PATH, accessToken, KakaoUserInfoResponse.class);
    }

    // 어드민 키로 카카오 사용자와 서비스 앱의 연결 해제
    public KakaoUnlinkResponse unlink(String providerUserId) {
        String adminKey = properties.getAdminKey();
        if (adminKey == null || adminKey.isBlank()) {
            throw new AuthException(AuthErrorCode.KAKAO_UNLINK_FAILED);
        }

        String requestBody = "target_id_type=user_id&target_id="
                + URLEncoder.encode(providerUserId, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getApiBaseUrl() + UNLINK_PATH))
                .timeout(REQUEST_TIMEOUT)
                .header("Authorization", "KakaoAK " + adminKey)
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded;charset=utf-8"
                )
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AuthException(AuthErrorCode.KAKAO_UNLINK_FAILED);
            }

            return objectMapper.readValue(response.body(), KakaoUnlinkResponse.class);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AuthException(AuthErrorCode.KAKAO_UNLINK_FAILED);
        } catch (IOException | IllegalArgumentException exception) {
            throw new AuthException(AuthErrorCode.KAKAO_UNLINK_FAILED);
        }
    }

    private <T> T get(String path, String accessToken, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getApiBaseUrl() + path))
                .timeout(REQUEST_TIMEOUT)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            validateStatus(response.statusCode());
            return objectMapper.readValue(response.body(), responseType);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AuthException(AuthErrorCode.KAKAO_API_CALL_FAILED);
        } catch (IOException | IllegalArgumentException exception) {
            throw new AuthException(AuthErrorCode.KAKAO_API_CALL_FAILED);
        }
    }

    private void validateStatus(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }

        if (statusCode == 400 || statusCode == 401) {
            throw new AuthException(AuthErrorCode.INVALID_KAKAO_TOKEN);
        }

        throw new AuthException(AuthErrorCode.KAKAO_API_CALL_FAILED);
    }
}
