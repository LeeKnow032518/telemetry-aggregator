package io.leantech.telemetryaggregator.schedule;

import io.leantech.telemetryaggregator.service.AggregationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private final AggregationService aggregationService;

    // Makes query every minute
    @Scheduled(fixedRate = 60000)
    public void runEveryFiveMinutes() {
        Instant queryBoundLower = Instant.now().minus(2, ChronoUnit.MINUTES);
        Instant queryBoundUpper = Instant.now().minus(1, ChronoUnit.MINUTES);

        log.info("Making cassandra query: " + Instant.now());
        aggregationService.metricsMonitoringForAggregation(queryBoundLower, queryBoundUpper);
    }
}
