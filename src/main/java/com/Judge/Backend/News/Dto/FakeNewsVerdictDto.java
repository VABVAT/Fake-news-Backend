package com.Judge.Backend.News.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakeNewsVerdictDto {
    @JsonProperty("verdict")
    String verdict;

    @JsonProperty("confidence")
    String confidence;

    @JsonProperty("evidence")
    List<Evidence> evidence;

    @JsonProperty("recommended_actions")
    String recommendedAction;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class Evidence {
        String quote;    // quoted claim or sentence from the input
        String source;   // url or authoritative source (or "none found")
        Double score;    // optional relevance/confidence for this piece of evidence
    }
}
