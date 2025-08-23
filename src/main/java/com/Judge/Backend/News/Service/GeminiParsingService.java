package com.Judge.Backend.News.Service;

import com.Judge.Backend.News.Dto.FakeNewsVerdictDto;
import com.Judge.Backend.News.Dto.RequestDto;
import com.Judge.Backend.News.Util.GeminiConfig;
import com.Judge.Backend.News.Util.GeminiPrompt;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeminiParsingService {
    GeminiPrompt geminiPrompt;
    GeminiConfig geminiConfig;


    public FakeNewsVerdictDto fakeNews(RequestDto dto) {
        try {
            String prompt = geminiPrompt.buildPrompt(dto);
            String apiKey = geminiConfig.getGeminiApiKey();


        } catch (Exception e) {
            // Proper error handling

        }
    }
}
