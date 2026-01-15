package io.leantech.telemetryaggregator.service;

import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import io.leantech.telemetryaggregator.service.cassandra.CassandraService;
import io.leantech.telemetryaggregator.service.postgres.DeviceService;
import io.leantech.telemetryaggregator.service.postgres.RouterService;
import io.leantech.telemetryaggregator.service.postgres.ServiceService;
import io.leantech.telemetryaggregator.utils.cassandra.DataUtilsCassandra;
import io.leantech.telemetryaggregator.utils.cassandra.SliceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AggregationServiceTests {

    @InjectMocks
    private AggregationService aggregationService;

    private int batchSize;

    @Mock
    private DeviceService deviceService;

    @Mock
    private ServiceService serviceService;

    @Mock
    private RouterService routerService;

    @Mock
    private CassandraService cassandraService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aggregationService, "batchSize", 2000);
    }

    @Test
    @DisplayName("Test aggregate all data from Cassandra with few pages functionality")
    public void givenInstantBounds_whenAggregateAllDataFromCassandra_thenCassandraServiceIsCalled(){
        // given
        Instant lower = Instant.now().minus(Duration.ofMinutes(1));
        Instant upper = Instant.now();
        String hour = lower.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));

        long totalRecords = 5000;

        when(cassandraService.readNumberOfRecords(eq(hour), eq(lower), eq(upper)))
                .thenReturn(totalRecords);

        Slice<TelemetryEntity> firstPage = SliceBuilder.slice(Collections.nCopies(1000, mock(TelemetryEntity.class)))
                .hasNext(true).build();
        Slice<TelemetryEntity> middlePage = SliceBuilder.slice(Collections.nCopies(1000, mock(TelemetryEntity.class)))
                .hasNext(true).build();
        Slice<TelemetryEntity> lastPage = SliceBuilder.slice(Collections.nCopies(1000, mock(TelemetryEntity.class)))
                .hasNext(false).build();

        when(cassandraService.readFromCassandra(eq(hour), eq(lower), eq(upper), any(Pageable.class)))
                .thenReturn(firstPage)
                .thenReturn(middlePage)
                .thenReturn(middlePage)
                .thenReturn(middlePage)
                .thenReturn(lastPage);

        // when
        aggregationService.aggregateAllDataFromCassandra(lower, upper);

        // then
        verify(cassandraService, times(5)).readFromCassandra(eq(hour), eq(lower), eq(upper), any(Pageable.class));
        verify(deviceService, times(5)).aggregateDevice(anyList());
        verify(routerService, times(5)).aggregateRouter(anyList());
        verify(serviceService, times(5)).aggregateService(anyList());
    }

    @Test
    @DisplayName("Test aggregate functionality")
    public void givenListOfTelemetryEntity_whenAggregate_thenAllServicesAreCalled(){
        //given
        TelemetryEntity telemetry1 = DataUtilsCassandra.getTelemetryEventFirst();
        TelemetryEntity telemetry2 = DataUtilsCassandra.getTelemetryEventSecond();

        List<TelemetryEntity> telemetries = List.of(telemetry2, telemetry1);
        //when
        aggregationService.aggregate(telemetries);
        //then
        verify(routerService, times(1)).aggregateRouter(telemetries);

        verify(deviceService, times(1)).aggregateDevice(anyList());

        verify(serviceService, times(1)).aggregateService(anyList());
    }
}
