package com.Judge.Backend.News.Controller;

import com.Judge.Backend.News.Dto.RequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {
    @PostMapping("/verify/news")
    public ResponseEntity<?> verifyNews(RequestDto requestDto) {

        return ResponseEntity.ok().build();
    }
}
