package com.Judge.Backend.News.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("reasoning")
    String reasoning;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class Evidence {
        @JsonProperty("quote")
        String quote;    // quoted claim or sentence from the input

        @JsonProperty("source")
        String source;   // url or authoritative source (or "none found")
    }
}
