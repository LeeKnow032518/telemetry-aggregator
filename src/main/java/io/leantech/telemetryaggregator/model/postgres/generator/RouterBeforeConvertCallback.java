package io.leantech.telemetryaggregator.model.postgres.generator;

import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RouterBeforeConvertCallback implements BeforeConvertCallback<AggregatedRouterStats> {
    @Override
    public AggregatedRouterStats onBeforeConvert(AggregatedRouterStats router) {
        if (router.getId() == null) {
            router.setId(UUID.randomUUID());
            log.debug("Generated new UUID: {}", router.getId());
        }
        return router;
    }
}
