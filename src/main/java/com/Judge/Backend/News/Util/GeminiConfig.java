package com.Judge.Backend.News.Util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeminiConfig {

    @Value("${gemini.api.key}")
    String geminiApiKey;

    @Value("${gemini.api.endpoint}")
    String endPoint;
}
