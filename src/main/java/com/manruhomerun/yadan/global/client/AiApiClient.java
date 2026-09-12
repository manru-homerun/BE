package com.manruhomerun.yadan.global.client;

import java.net.URI;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.manruhomerun.yadan.global.error.exception.ExternalApiCallException;
import com.manruhomerun.yadan.global.properties.AiApiProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiApiClient {

    private final AiApiProperties aiApiProperties;

    public <T> T generateTravel(Object requestBody, Class<T> responseType) {
        String path = aiApiProperties.getTravelGeneratePath();
        URI requestUri = UriComponentsBuilder.fromUriString(aiApiProperties.getBaseUrl())
                .path(path)
                .build()
                .encode()
                .toUri();

        try {
            T response = RestClient.create()
                    .post()
                    .uri(requestUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, apiResponse) -> {
                        throw new ExternalApiCallException(
                                "AI API 호출에 실패했습니다. "
                                        + "path=" + path + "\n"
                                        + "status=" + apiResponse.getStatusCode()
                        );
                    })
                    .body(responseType);

            if (response == null) {
                throw new ExternalApiCallException("AI API 응답이 비어 있습니다. path=" + path);
            }

            return response;
        } catch (RestClientException exception) {
            throw new ExternalApiCallException("AI API 호출에 실패했습니다. path=" + path);
        }
    }
}
