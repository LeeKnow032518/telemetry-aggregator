package io.leantech.telemetryaggregator.model.postgres.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ServiceBeforeConvertCallback implements BeforeConvertCallback<AggregatedServiceStats> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public AggregatedServiceStats onBeforeConvert(AggregatedServiceStats service) {
        if (service.getId() == null) {
            service.setId(UUID.randomUUID());
            log.debug("Generated new UUID: {}", service.getId());
        }

//        if (service.getQualityCounts() != null && service.getQualityCountsJson() == null) {
//            try {
//                service.setQualityCountsJson(mapper.writeValueAsString(service.getQualityCounts()));
//            } catch (Exception e) {
//                throw new RuntimeException("Ошибка сериализации качества", e);
//            }
//        }
        return service;
    }
}
