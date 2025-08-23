package com.Judge.Backend.News.Controller;

import com.Judge.Backend.News.Dto.FakeNewsVerdictDto;
import com.Judge.Backend.News.Dto.RequestDto;
import com.Judge.Backend.News.Service.GeminiParsingService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsController {
    GeminiParsingService geminiParsingService;
    @PostMapping("/verify/news")
    public ResponseEntity<FakeNewsVerdictDto> verifyNews(@RequestBody RequestDto requestDto) {

        FakeNewsVerdictDto response = geminiParsingService.fakeNews(requestDto);
        return ResponseEntity.ok(response);
    }
}
