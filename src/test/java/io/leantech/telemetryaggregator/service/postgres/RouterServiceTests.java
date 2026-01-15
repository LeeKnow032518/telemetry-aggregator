package io.leantech.telemetryaggregator.service.postgres;

import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import io.leantech.telemetryaggregator.model.cassandra.udt.DeviceUdt;
import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import io.leantech.telemetryaggregator.repository.postgres.RouterRepository;
import io.leantech.telemetryaggregator.utils.cassandra.DataUtilsCassandra;
import io.leantech.telemetryaggregator.utils.postgres.DataUtilsRouter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RouterServiceTests {

    @Mock
    private RouterRepository routerRepository;

    private final MeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, Clock.SYSTEM);

    @Mock
    private DistributionSummary recordsSaved;

    @InjectMocks
    private RouterService routerService;

    @BeforeEach
    public void setUp(){
        routerService = new RouterService(registry, routerRepository);
        routerService.init();
    }

    @Test
    @DisplayName("Test aggregate router functionality")
    public void givenListOfTelemetryEntity_whenAggregateRouter_thenRepositoryIsCalled(){
        //given
        List<TelemetryEntity> routers = List.of(DataUtilsCassandra.getTelemetryEventFirst(),
                DataUtilsCassandra.getTelemetryEventSecond(),
                DataUtilsCassandra.getTelemetryEventThird());
        //when
        routerService.aggregateRouter(routers);
        //then
        ArgumentCaptor<Iterable<AggregatedRouterStats>> captor = ArgumentCaptor.forClass((Class) Iterable.class);
        verify(routerRepository, times(1)).saveAll(captor.capture());

        List<AggregatedRouterStats> savedList = new ArrayList<>();
        captor.getValue().forEach(savedList::add);
    }

    @Test
    @DisplayName("Test aggregation part functionality")
    public void givenListOfTelemetryEntities_whenAggregationPart_thenMapStringAggregatedRouterStatsIsReturned(){
        //given
        List<TelemetryEntity> routersForAggregation = List.of(DataUtilsCassandra.getTelemetryEventFirst(),
                DataUtilsCassandra.getTelemetryEventSecond(),
                DataUtilsCassandra.getTelemetryEventThird());
        //when
        Map<String, AggregatedRouterStats> aggregatedRouterStats = routerService.aggregationPart(routersForAggregation);
        Map<String, AggregatedRouterStats> expectedResult = Map.of("router-967herman.io", DataUtilsRouter.getRouterHermanTransient(),
                "router-100weissant.io", DataUtilsRouter.getRouterWeissantTransient());

        //then
        assertThat(aggregatedRouterStats.isEmpty()).isFalse();
        assertThat(aggregatedRouterStats.size()).isEqualTo(2);
        assertThat(aggregatedRouterStats.keySet()).isEqualTo(expectedResult.keySet());

        for(String key : aggregatedRouterStats.keySet()){
            assertThat(aggregatedRouterStats.get(key).getHostname()).isEqualTo(expectedResult.get(key).getHostname());
            assertThat(aggregatedRouterStats.get(key).getDomain()).isEqualTo(expectedResult.get(key).getDomain());
            assertThat(aggregatedRouterStats.get(key).getTotalInTraffic()).isEqualTo(expectedResult.get(key).getTotalInTraffic());
            assertThat(aggregatedRouterStats.get(key).getTotalOutTraffic()).isEqualTo(expectedResult.get(key).getTotalOutTraffic());
            assertThat(aggregatedRouterStats.get(key).getAvgLatency()).isEqualTo(expectedResult.get(key).getAvgLatency());
            assertThat(aggregatedRouterStats.get(key).getAvgScore()).isEqualTo(expectedResult.get(key).getAvgScore());
            assertThat(aggregatedRouterStats.get(key).getUniqueDevices()).isEqualTo(expectedResult.get(key).getUniqueDevices());
        }
    }

    @Test
    @DisplayName("Test count service functionality")
    public void givenListOfRouterUdt_whenCountServices_thenMapOfStringIntegerIsReturned(){
        //given
        List<TelemetryEntity> routers = List.of(DataUtilsCassandra.getTelemetryEventFirst(),
                DataUtilsCassandra.getTelemetryEventSecond(),
                DataUtilsCassandra.getTelemetryEventThird());
        //when
        Map<String, Integer> countServices = routerService.countServices(routers);
        Map<String, Integer> expected = Map.of("router-967herman.io", 2,
                                                "router-100weissant.io", 1);
        //then
        assertThat(countServices.isEmpty()).isFalse();
        assertThat(countServices.keySet().size()).isEqualTo(2);
        for(String key : countServices.keySet()){
            assertThat(countServices.get(key)).isEqualTo(expected.get(key));
        }
    }

    @Test
    @DisplayName("Test calculate key functionality")
    public void givenTelemetryEntity_whenCalculateKey_thenStringIsReturned(){
        //given
        TelemetryEntity router = DataUtilsCassandra.getTelemetryEventFirst();
        //when
        String keyForTest = routerService.calculateKey(router);
        String expected = "router-967herman.io";
        //then
        assertThat(keyForTest.isEmpty()).isFalse();
        assertThat(keyForTest).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test count unique device functionality")
    public void givenListOfTelemetryEntity_whenCountUniquiDevice_thenMapOfStringSetOfDeviceUdtIsReturned(){
        //given
        List<TelemetryEntity> routers = List.of(DataUtilsCassandra.getTelemetryEventFirst(),
                DataUtilsCassandra.getTelemetryEventSecond(),
                DataUtilsCassandra.getTelemetryEventThird());
        //when
        Map<String, Set<DeviceUdt>> uniqueDevices = routerService.countUniqueDevices(routers);
        Map<String, Set<DeviceUdt>> expected= Map.of("router-967herman.io", Set.of(DataUtilsCassandra.getDeviceFirst()),
                                                    "router-100weissant.io", Set.of(DataUtilsCassandra.getDeviceDifferent()));
        //then
        assertThat(uniqueDevices.isEmpty()).isFalse();
        assertThat(uniqueDevices.keySet()).isEqualTo(expected.keySet());
        for(String key : uniqueDevices.keySet()){
            assertThat(uniqueDevices.get(key)).isEqualTo(expected.get(key));
        }
    }

    @Test
    @DisplayName("Test unite two routers functionality")
    public void givenTwoAggregatedRoutersStats_whenUniteTwoRouters_thenOneAggregatedRouterStatsIsReturned(){
        //given
        AggregatedRouterStats router1 = DataUtilsRouter.getRouterWeissantTransient();
        AggregatedRouterStats router2 = DataUtilsRouter.getRouterWeissantForUniteTransient();
        //when
        AggregatedRouterStats unitedRouter = routerService.uniteTwoRouters(router1, router2);
        AggregatedRouterStats expected = DataUtilsRouter.getRouterWeissantUnitedTransient();
        //then
        assertThat(unitedRouter).isNotNull();
        assertThat(unitedRouter.getDomain()).isEqualTo(expected.getDomain());
        assertThat(unitedRouter.getHostname()).isEqualTo(expected.getHostname());
        assertThat(unitedRouter.getAvgLatency()).isEqualTo(expected.getAvgLatency());
        assertThat(unitedRouter.getAvgScore()).isEqualTo(expected.getAvgScore());
        assertThat(unitedRouter.getTotalOutTraffic()).isEqualTo(expected.getTotalOutTraffic());
        assertThat(unitedRouter.getTotalInTraffic()).isEqualTo(expected.getTotalInTraffic());
    }
}
