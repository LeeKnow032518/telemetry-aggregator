package io.leantech.telemetryaggregator.config.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

@ReadingConverter
public class JsonbToMapConverter implements Converter<String, Map<String, Integer>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Integer> convert(String source) {
        try {
            return objectMapper.readValue(source, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON string to Map", e);
        }
    }
}

