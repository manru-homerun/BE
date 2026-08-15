package com.manruhomerun.yadan.global.client;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import com.manruhomerun.yadan.global.error.exception.ExternalApiCallException;
import com.manruhomerun.yadan.global.properties.TourApiProperties;
import com.manruhomerun.yadan.travelspot.dto.TourApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class ExternalApiClient {

    private final TourApiProperties tourApiProperties;
    private final ObjectMapper objectMapper;

    public <T extends TourApiResponse> T get(
            String path,                // uri
            Map<String, ?> queryParams, // 쿼리 파라미터
            Class<T> responseType       // 응답 타입
    ) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(tourApiProperties.getBaseUrl())
                .path(path);

        uriBuilder.queryParam("MobileApp", tourApiProperties.getMobileApp());
        uriBuilder.queryParam("MobileOS", tourApiProperties.getMobileOs());
        uriBuilder.queryParam("_type", "json");
        uriBuilder.queryParam("serviceKey", tourApiProperties.getServiceKey());

        if (queryParams != null) {
            for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
                uriBuilder.queryParam(entry.getKey(), entry.getValue());
            }
        }

        URI requestUri = uriBuilder.encode().build().toUri();

        try {
            String responseBody = RestClient.create()
                    .get()
                    .uri(requestUri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ExternalApiCallException(
                                "외부 API 호출에 실패했습니다. " +
                                "path=" + path + "\n" +
                                "status=" + response.getStatusCode()
                        );
                    })
                    .body(String.class);

            T response = objectMapper.readValue(responseBody, responseType);

            if (response == null) {
                throw new ExternalApiCallException(
                        "외부 API 응답이 비어 있습니다. path=" + path
                );
            }
            if (response.getResultCode() == null) {
                throw new ExternalApiCallException(
                        "외부 API 응답 헤더가 올바르지 않습니다. path=" + path
                );
            }
            if (!"0000".equals(response.getResultCode())) {
                throw new ExternalApiCallException(
                        "외부 API 호출에 실패했습니다. path=" + path
                        + "\nerrorMessage=" + response.getResultMessage()
                );
            }

            return response;
        } catch (JsonProcessingException exception) {
            throw new ExternalApiCallException(
                    "외부 API 응답 파싱에 실패했습니다. path=" + path
            );
        } catch (RestClientException exception) {
            throw new ExternalApiCallException(
                    "외부 API 호출에 실패했습니다. path=" + path
            );
        }
    }
}
