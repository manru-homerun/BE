package com.manruhomerun.yadan.global.client;

import java.util.Map;

import com.manruhomerun.yadan.global.error.exception.ExternalApiCallException;
import com.manruhomerun.yadan.global.properties.TourApiProperties;
import com.manruhomerun.yadan.travelspot.dto.TourApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class ExternalApiClient {
    private final TourApiProperties tourApiProperties;

    public <T extends TourApiResponse> T get(String path, Map<String, ?> queryParams, Class<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tourApiProperties.getBaseUrl())
                .path(path);

        uriBuilder.queryParam("MobileApp", tourApiProperties.getMobileApp());
        uriBuilder.queryParam("MobileOS", tourApiProperties.getMobileOs());
        uriBuilder.queryParam("_type", "json");
        uriBuilder.queryParam("serviceKey", tourApiProperties.getServiceKey());

        if(queryParams != null){
            for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
                uriBuilder.queryParam(entry.getKey(), entry.getValue());
            }
        }

        try {
            T response = RestClient.create()
                    .get()
                    .uri(uriBuilder.encode().toUriString())
                    .retrieve()
                    .body(responseType);

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
        } catch (RestClientException exception) {
            throw new ExternalApiCallException(
                    "외부 API 호출에 실패했습니다. path=" + path
            );
        }
    }
}
