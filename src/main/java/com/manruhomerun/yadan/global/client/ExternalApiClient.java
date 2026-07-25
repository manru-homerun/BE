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

    public <T extends TourApiResponse> T get(String path, Map<String, ?> queryParams, Class<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tourApiProperties.getBaseUrl())
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
                        String errorBody = new String(response.getBody().readAllBytes());

                        System.out.println("외부 API 실패 응답 body: " + errorBody);

                        throw new ExternalApiCallException(
                                "외부 API 호출에 실패했습니다. path=" + path
                                        + "\nstatus=" + response.getStatusCode()
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
        } catch (ExternalApiCallException exception) {
            throw exception;
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
