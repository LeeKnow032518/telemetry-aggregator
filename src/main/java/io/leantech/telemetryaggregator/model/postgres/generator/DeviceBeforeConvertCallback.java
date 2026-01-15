package io.leantech.telemetryaggregator.model.postgres.generator;

import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DeviceBeforeConvertCallback implements BeforeConvertCallback<AggregatedDeviceStats> {

    @Override
    public AggregatedDeviceStats onBeforeConvert(AggregatedDeviceStats device) {
        if (device.getId() == null) {
            device.setId(UUID.randomUUID());
            log.debug("Generated new UUID: {}", device.getId());
        }
        return device;
    }
}