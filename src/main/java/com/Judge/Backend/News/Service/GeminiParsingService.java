package com.Judge.Backend.News.Service;

import com.Judge.Backend.News.Dto.FakeNewsVerdictDto;
import com.Judge.Backend.News.Dto.RequestDto;
import com.Judge.Backend.News.Util.GeminiPrompt;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor()
public class GeminiParsingService {
    GeminiPrompt geminiPrompt;

    FakeNewsVerdictDto fakeNews(RequestDto dto) {
        String GeminiPrompt = geminiPrompt.buildPrompt(dto);
        return null;
    }

}