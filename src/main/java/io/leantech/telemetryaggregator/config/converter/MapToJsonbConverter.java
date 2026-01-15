package io.leantech.telemetryaggregator.config.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

@WritingConverter
public class MapToJsonbConverter implements Converter<Map<String, Integer>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(Map<String, Integer> source) {
            return source.toString();
    }
}

