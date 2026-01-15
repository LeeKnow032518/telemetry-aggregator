package io.leantech.telemetryaggregator.service;

import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import io.leantech.telemetryaggregator.model.cassandra.udt.DeviceUdt;
import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceUdt;
import io.leantech.telemetryaggregator.service.cassandra.CassandraService;
import io.leantech.telemetryaggregator.service.postgres.DeviceService;
import io.leantech.telemetryaggregator.service.postgres.RouterService;
import io.leantech.telemetryaggregator.service.postgres.ServiceService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

    @Value("${spring.cassandra.batch-size}")
    private int batchSize;
    private final MeterRegistry registry;

    private final RouterService routerService;
    private final DeviceService deviceService;
    private final ServiceService serviceService;

    private final CassandraService cassandraService;

    public Timer init(){
        return Timer.builder("aggregation.cycle.duration")
                .description("Full aggregation cycle duration")
                .tag("component", "aggregation-service")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    public void metricsMonitoringForAggregation(Instant boundLower, Instant boundUpper){
        Timer fullCycleTimer = init();
        fullCycleTimer.record(() -> aggregateAllDataFromCassandra(boundLower, boundUpper));
    }

    public void aggregateAllDataFromCassandra(Instant boundLower, Instant boundUpper){
            String hour = boundLower.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));
            long numOfRecords = cassandraService.readNumberOfRecords(hour, boundLower, boundUpper);
            if (numOfRecords == 0) {
                log.info("No records for aggregation");
                return;
            }

            log.info(numOfRecords + " are available for aggregation");
            Slice<TelemetryEntity> slice;

            Pageable pageable = CassandraPageRequest.first(batchSize);

            do {
                slice = cassandraService.readFromCassandra(hour, boundLower, boundUpper, pageable);

                if (slice.hasContent()) {
                    List<TelemetryEntity> records = new ArrayList<>();
                    slice.forEach(records::add);

                    log.info("Start aggregation of " + records.size() + " records");
                    aggregate(records);
                    log.info("Aggregation of " + records.size() + " finished");
                }
                pageable = slice.nextPageable();

            } while (slice.hasNext());
    }

    public void aggregate(List<TelemetryEntity> telemetryEntities){
        routerService.aggregateRouter(telemetryEntities);

        List<DeviceUdt> devices = telemetryEntities.stream()
                .flatMap(router -> router.getDevices().values().stream()).toList();
        deviceService.aggregateDevice(devices);

        List<ServiceUdt> services = devices.stream()
                .flatMap(device -> device.getServices().values().stream()).toList();
        serviceService.aggregateService(services);
    }

}
