package com.manruhomerun.yadan.global.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manruhomerun.yadan.global.error.exception.ExternalApiCallException;
import com.manruhomerun.yadan.global.properties.AiApiProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiApiClient {

    private final AiApiProperties aiApiProperties;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(AiApiClient.class);

    public <T> T generateTravel(Object requestBody, Class<T> responseType) {
        String path = aiApiProperties.getTravelGeneratePath();
        URI requestUri = UriComponentsBuilder.fromUriString(aiApiProperties.getBaseUrl())
                .path(path)
                .build()
                .encode()
                .toUri();

        logger.info("AI API 요청 시작 uri={}, body={}", requestUri, requestBody);

        try {
            String responseBody = RestClient.create()
                    .post()
                    .uri(requestUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, apiResponse) -> {
                        String errorResponseBody = new String(
                                apiResponse.getBody().readAllBytes(),
                                StandardCharsets.UTF_8
                        );
                        logger.error(
                                "AI API 오류 응답 status={}, body={}",
                                apiResponse.getStatusCode(),
                                errorResponseBody
                        );
                        throw new ExternalApiCallException(
                                "AI API 호출에 실패했습니다. "
                                        + "path=" + path + "\n"
                                        + "status=" + apiResponse.getStatusCode()
                        );
                    })
                    .body(String.class);

            System.out.println("AI API 응답 body: " + responseBody);

            if (responseBody == null || responseBody.isBlank()) {
                throw new ExternalApiCallException("AI API 응답이 비어 있습니다. path=" + path);
            }

            return objectMapper.readValue(responseBody, responseType);
        } catch (JsonProcessingException exception) {
            logger.error("AI API 응답 파싱 실패 path={}", path, exception);
            throw new ExternalApiCallException("AI API 응답 파싱에 실패했습니다. path=" + path);
        } catch (RestClientException exception) {
            logger.error("AI API 통신 실패 uri={}", requestUri, exception);
            throw new ExternalApiCallException("AI API 호출에 실패했습니다. path=" + path);
        }
    }

    public void recommendTravelSpots(Object requestBody) {
        String path = aiApiProperties.getTravelRecommendPath();
        URI requestUri = UriComponentsBuilder.fromUriString(aiApiProperties.getBaseUrl())
                .path(path)
                .build()
                .encode()
                .toUri();

        logger.info("AI API 요청 시작 uri={}, body={}", requestUri, requestBody);

        try {
            String responseBody = RestClient.create()
                    .post()
                    .uri(requestUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, apiResponse) -> {
                        String errorResponseBody = new String(
                                apiResponse.getBody().readAllBytes(),
                                StandardCharsets.UTF_8
                        );
                        logger.error("AI API 오류 응답 status={}, body={}", apiResponse.getStatusCode(), errorResponseBody);
                        throw new ExternalApiCallException(
                                "AI API 호출에 실패했습니다. path=" + path
                                        + "\nstatus=" + apiResponse.getStatusCode()
                        );
                    })
                    .body(String.class);

            System.out.println("AI 여행지 추천 응답 body: " + responseBody);
        } catch (RestClientException exception) {
            logger.error("AI API 통신 실패 uri={}", requestUri, exception);
            throw new ExternalApiCallException("AI API 호출에 실패했습니다. path=" + path);
        }
    }
}
