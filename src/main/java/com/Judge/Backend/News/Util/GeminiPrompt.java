package com.Judge.Backend.News.Util;

import com.Judge.Backend.News.Dto.RequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
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

        b.append("Output Schema (exact JSON keys and types):\n");
        b.append("Produce a single JSON object with the following shape — do not add any other keys:\n\n");

        b.append("{\n");
        b.append("  \"verdict\": \"<STRING: one of TRUE | FALSE | MISLEADING | NOT_ENOUGH_INFO>\",\n");
        b.append("  \"confidence\": <NUMBER between 0 and 1>,\n");
        b.append("  \"reasoning\": \"<STRING: 3-6 concise sentences explaining the judgment>\",\n");
        b.append("  \"evidence\": [\n");
        b.append("    {\n");
        b.append("      \"quote\": \"<STRING: quoted sentence or claim>\",\n");
        b.append("      \"source\": \"<STRING: authoritative URL or 'none found'>\"\n");
        b.append("    }\n");
        b.append("    // up to 3 items\n");
        b.append("  ],\n");
        b.append("  \"recommended_actions\": \"<STRING: short suggested next steps>\"\n");
        b.append("}\n\n");

        b.append("Important formatting rules:\n");
        b.append("- Return valid JSON only. No surrounding backticks, markdown, or prose.\n");
        b.append("- Use the exact key names shown above (verdict, confidence, reasoning, evidence, recommended_actions).\n");
        b.append("- evidence must be an array (can be empty []). Each evidence item must contain exactly 'quote' and 'source'.\n");
        b.append("- confidence must be a number (e.g. 0.95). If uncertain, choose a lower confidence.\n");
        b.append("- Keep reasoning concise (3-6 sentences) and factual; avoid speculation.\n");



        return b.toString();
    }

    String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
