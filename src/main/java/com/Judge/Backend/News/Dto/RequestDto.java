package com.Judge.Backend.News.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {

    @JsonProperty("info_type")
    String infoType;

    @JsonProperty("headline")
    String headline;

    @JsonProperty("information")
    String information;

    @JsonProperty("meta_data")
    String metaData;
}
