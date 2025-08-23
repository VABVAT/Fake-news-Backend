package com.Judge.Backend.News.Service;

import com.Judge.Backend.HttpClient.HttpRequestClient;
import com.Judge.Backend.News.Dto.FakeNewsVerdictDto;
import com.Judge.Backend.News.Dto.RequestDto;
import com.Judge.Backend.News.Util.GeminiConfig;
import com.Judge.Backend.News.Util.GeminiPrompt;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeminiParsingService {
    GeminiPrompt geminiPrompt;
    GeminiConfig geminiConfig;
    HttpRequestClient httpRequestClient;
    GeminiResponseHandler geminiResponseHandler;

    public FakeNewsVerdictDto fakeNews(RequestDto dto) {
        try {
            String prompt = geminiPrompt.buildPrompt(dto);
            String apiKey = geminiConfig.getGeminiApiKey();
            String url = geminiConfig.getEndPoint();

            Map<String, String> headers = Map.of(
                    "x-goog-api-key", apiKey.trim()
            );

            Map<String, Object> body = Map.of(
                    "contents", java.util.List.of(
                            Map.of("parts", java.util.List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "tools", java.util.List.of(
                            Map.of("google_search", Map.of())
                    )
            );

            ResponseEntity<String> response = httpRequestClient.execute(url, HttpMethod.POST, headers, body, String.class, null);
            FakeNewsVerdictDto rdto = geminiResponseHandler.parseFirstDtoFromResponseEntity(response).orElseThrow(() -> new IllegalStateException("Parsing unsuccessful"));
            log.info("Gemini response: {}", response);
            return rdto;

        } catch (Exception e) {
            // Proper error handling
            log.warn("Failed to parse response wrapper as JSON; trying raw extraction: {}", e.getMessage());
        }
        return null;
    }
}
