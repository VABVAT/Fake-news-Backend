package com.Judge.Backend.News.Service;// place inside your GeminiResponseHandler (or a new helper class)
// assumes you have ObjectMapper mapper injected and methods extractFencedOrFirstJson / extractJsonFromFencedBlock / extractFirstJsonObject available

import com.Judge.Backend.News.Dto.FakeNewsVerdictDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeminiResponseHandler {

    private final ObjectMapper mapper;

    /**
     * Parse ResponseEntity body and return first successfully deserialized FakeNewsVerdictDto.
     */
    public Optional<FakeNewsVerdictDto> parseFirstDtoFromResponseEntity(ResponseEntity<String> responseEntity) {
        String body = responseEntity == null ? null : responseEntity.getBody();
        if (body == null || body.isBlank()) return Optional.empty();

        try {
            JsonNode root = mapper.readTree(body);
            // 1) If wrapper contains candidates -> iterate
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray()) {
                for (JsonNode candidate : candidates) {
                    JsonNode parts = candidate.path("content").path("parts");
                    if (parts.isArray()) {
                        for (JsonNode part : parts) {
                            JsonNode textNode = part.path("text");
                            if (textNode.isTextual()) {
                                String candidateText = textNode.asText();
                                String innerJson = extractFencedOrFirstJson(candidateText);
                                if (innerJson != null) {
                                    try {
                                        ObjectNode normalized = normalizeNumericKeysToDtoJson(innerJson);
                                        FakeNewsVerdictDto dto = mapper.treeToValue(normalized, FakeNewsVerdictDto.class);
                                        return Optional.of(dto);
                                    } catch (Exception e) {
                                        log.warn("Normalization/deserialization failed for candidate part - trying next", e);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2) Fallback: try to extract JSON directly from body
            String innerJson = extractFencedOrFirstJson(body);
            if (innerJson != null) {
                ObjectNode normalized = normalizeNumericKeysToDtoJson(innerJson);
                return Optional.of(mapper.treeToValue(normalized, FakeNewsVerdictDto.class));
            }

            // 3) Last resort: try mapping root itself (safe if DTO ignores unknowns)
            try {
                FakeNewsVerdictDto dto = mapper.treeToValue(root, FakeNewsVerdictDto.class);
                return Optional.of(dto);
            } catch (Exception ignored) {
            }

        } catch (IOException e) {
            log.error("Failed to parse Gemini response body", e);
        }

        return Optional.empty();
    }

    /**
     * Normalize a JSON string that may use numeric keys into an ObjectNode matching your DTO.
     * Handles:
     * - "1" -> verdict
     * - "2" -> confidence (as string)
     * - "3" -> recommended_actions (reasoning)
     * - "4" -> evidence (array) with url -> source
     *
     * If the input is already keyed correctly, it still performs small normalizations (confidence -> string, evidence.url -> source).
     */
    private ObjectNode normalizeNumericKeysToDtoJson(String json) throws IOException {
        JsonNode node = mapper.readTree(json);
        if (!node.isObject()) {
            // wrap into an object node as fallback
            ObjectNode wrap = mapper.createObjectNode();
            wrap.setAll((ObjectNode) mapper.readTree("{}"));
            return wrap;
        }

        ObjectNode obj = (ObjectNode) node;

        // If numeric keys exist, map them
        if (obj.has("1")) {
            // verdict: map whatever is in "1" to "verdict"
            obj.set("verdict", obj.get("1"));
            obj.remove("1");
        }
        if (obj.has("2")) {
            // confidence may be numeric; convert to text to match DTO (String)
            JsonNode conf = obj.get("2");
            obj.put("confidence", conf == null || conf.isNull() ? null : conf.asText());
            obj.remove("2");
        } else {
            // ensure existing confidence is textual
            JsonNode conf = obj.get("confidence");
            if (conf != null && !conf.isTextual()) obj.put("confidence", conf.asText());
        }
        if (obj.has("3")) {
            // reasoning -> recommended_actions (if DTO expects recommended_actions)
            obj.set("recommended_actions", obj.get("3"));
            obj.remove("3");
        }
        // evidence: numeric "4" -> "evidence"
        if (obj.has("4")) {
            JsonNode ev = obj.get("4");
            obj.set("evidence", ev);
            obj.remove("4");
        }

        // Normalize evidence entries if present: url -> source
        JsonNode evidenceNode = obj.get("evidence");
        if (evidenceNode != null && evidenceNode.isArray()) {
            ArrayNode arr = (ArrayNode) evidenceNode;
            for (int i = 0; i < arr.size(); i++) {
                JsonNode evEntry = arr.get(i);
                if (evEntry.isObject()) {
                    ObjectNode evObj = (ObjectNode) evEntry;
                    // rename "url" or "source_url" to "source"
                    if (evObj.has("url")) {
                        evObj.set("source", evObj.get("url"));
                        evObj.remove("url");
                    }
                    if (evObj.has("source_url")) {
                        evObj.set("source", evObj.get("source_url"));
                        evObj.remove("source_url");
                    }
                    // ensure score is numeric or null (DTO expects Double)
                    JsonNode scoreNode = evObj.get("score");
                    if (scoreNode != null && scoreNode.isTextual()) {
                        try {
                            double d = Double.parseDouble(scoreNode.asText());
                            evObj.put("score", d);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }

        // As extra safety: if confidence still missing, set to empty string
        if (!obj.has("confidence")) {
            obj.put("confidence", "");
        } else {
            // ensure confidence is textual
            JsonNode c = obj.get("confidence");
            if (!c.isTextual()) obj.put("confidence", c.asText());
        }

        return obj;
    }

    // reuse or re-implement these helpers in your class:
    private String extractFencedOrFirstJson(String s) {
        // fenced-block extractor and balanced-json extractor (same logic as earlier)
        String fenced = extractJsonFromFencedBlock(s);
        if (fenced != null) return fenced;
        return extractFirstJsonObject(s);
    }

    private static final java.util.regex.Pattern FENCED_JSON_PATTERN = java.util.regex.Pattern.compile("(?s)```(?:json)?\\s*(\\{.*?\\})\\s*```");

    private String extractJsonFromFencedBlock(String s) {
        if (s == null) return null;
        java.util.regex.Matcher m = FENCED_JSON_PATTERN.matcher(s);
        if (m.find()) return m.group(1);
        return null;
    }

    private String extractFirstJsonObject(String s) {
        if (s == null) return null;
        int start = s.indexOf('{');
        if (start < 0) return null;
        int depth = 0;
        boolean inString = false;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) inString = !inString;
            if (inString) continue;
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return s.substring(start, i + 1);
            }
        }
        return null;
    }
}
