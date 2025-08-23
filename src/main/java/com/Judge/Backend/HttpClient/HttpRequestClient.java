package com.Judge.Backend.HttpClient;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HttpRequestClient {

    // Can be used other places as well
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    RestTemplate restTemplate;

    /**
     * Executes an HTTP request with flexible configuration.
     *
     * @param url          The target URL
     * @param method       The HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param headersMap   Custom headers
     * @param body         The request body (can be null for GET/DELETE)
     * @param responseType The expected response type (e.g., String.class, MyDto.class)
     * @param queryParams  Query parameters (can be null if none)
     * @param <T>          The type of response
     * @return ResponseEntity<T> containing status, headers, and body
     */
    public <T> ResponseEntity<T> execute(String url,
                                         HttpMethod method,
                                         Map<String, String> headersMap,
                                         Object body,
                                         Class<T> responseType,
                                         Map<String, String> queryParams) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (headersMap != null) {
            headersMap.forEach(headers::set);
        }

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(
                builder.toUriString(),
                method,
                entity,
                responseType
        );
    }
}
