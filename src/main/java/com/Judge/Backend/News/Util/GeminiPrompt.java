package com.Judge.Backend.News.Util;

import com.Judge.Backend.News.Dto.RequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiPrompt {

    public String buildPrompt(RequestDto dto) {
        StringBuilder b = new StringBuilder();
        // system instruction
        b.append("You are a rigorous, neutral fact-checker. ");
        b.append("Return ONLY valid JSON that matches the given schema. ");
        b.append("Do NOT add any extra commentary.\n\n");

        // user content: include the input fields explicitly
        b.append("INPUT:\n");
        b.append("Headline: ").append(nullToEmpty(dto.getHeadline())).append("\n\n");
        b.append("Information: ").append(nullToEmpty(dto.getInformation())).append("\n\n");
        b.append("Info type: ").append(nullToEmpty(dto.getInfoType())).append("\n\n");
        b.append("Meta: ").append(nullToEmpty(dto.getMetaData())).append("\n\n");

        // Instructions: what to evaluate and return
        b.append("TASK:\n");
        b.append("1) Evaluate whether the information is TRUE, FALSE, MISLEADING, or NOT_ENOUGH_INFO. ");
        b.append("2) Provide a confidence score [0-1].\n");
        b.append("3) Include concise 'reasoning' (3-6 sentences) explaining your judgment.\n");
        b.append("4) Extract up to 3 pieces of direct 'evidence' (quote + best available source URL or 'none found').\n");

        return b.toString();
    }

    String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
