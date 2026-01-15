package io.leantech.telemetryaggregator.it;

import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;
import io.leantech.telemetryaggregator.repository.cassandra.CassandraReadingRepository;
import io.leantech.telemetryaggregator.repository.postgres.DeviceRepository;
import io.leantech.telemetryaggregator.repository.postgres.RouterRepository;
import io.leantech.telemetryaggregator.repository.postgres.ServiceRepository;
import io.leantech.telemetryaggregator.service.AggregationService;
import io.leantech.telemetryaggregator.utils.cassandra.DataUtilsCassandra;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = "spring.profiles.active:test")
public class TelemetryAggregatorTest extends AbstractTelemetryAggregatorBaseTest{

    @Autowired
    private AggregationService aggregationService;

    @Autowired
    private CassandraReadingRepository cassandraRepository;

    @Autowired
    private RouterRepository routerRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    @DisplayName("Test telemetry aggregator functionality")
    public void collectDataFromCassandra_whenAggregateRecords_thenSaveToPostgres(){
        TelemetryEntity record1 = DataUtilsCassandra.getTelemetryEventFirst();
        TelemetryEntity record2 = DataUtilsCassandra.getTelemetryEventSecond();
        TelemetryEntity record3 = DataUtilsCassandra.getTelemetryEventThird();

        cassandraRepository.save(record1);
        cassandraRepository.save(record2);
        cassandraRepository.save(record3);

        aggregationService.aggregateAllDataFromCassandra(Instant.now().minus(1, ChronoUnit.MINUTES), Instant.now());

        Iterable<AggregatedRouterStats> routersIterable = routerRepository.findAll();
        List<AggregatedRouterStats> routers = new ArrayList<>();

        for(AggregatedRouterStats router : routersIterable){
            routers.add(router);
        }

        Iterable<AggregatedDeviceStats> devicesIterable = deviceRepository.findAll();
        List<AggregatedDeviceStats> devices = new ArrayList<>();

        for(AggregatedDeviceStats device : devicesIterable){
            devices.add(device);
        }

        Iterable<AggregatedServiceStats> servicesIterable = serviceRepository.findAll();
        List<AggregatedServiceStats> services = new ArrayList<>();

        for(AggregatedServiceStats service : servicesIterable){
            services.add(service);
        }

        assertThat(routers.isEmpty()).isFalse();
        assertThat(routers.size()).isEqualTo(2);

        assertThat(devices.isEmpty()).isFalse();
        assertThat(devices.size()).isEqualTo(2);

        assertThat(services.isEmpty()).isFalse();
        assertThat(services.size()).isEqualTo(2);
    }
}
