package io.leantech.telemetryaggregator.service.postgres;

import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceQualityUdt;
import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceUdt;
import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;
import io.leantech.telemetryaggregator.repository.postgres.ServiceRepository;
import io.leantech.telemetryaggregator.utils.cassandra.DataUtilsCassandra;
import io.leantech.telemetryaggregator.utils.postgres.DataUtilsService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceTests {

    @Mock
    private ServiceRepository serviceRepository;

    private final MeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, Clock.SYSTEM);

    @Mock
    private DistributionSummary recordsSaved;

    @InjectMocks
    private ServiceService serviceService;

    @BeforeEach
    public void setUp(){
        serviceService = new ServiceService(registry, serviceRepository);
        serviceService.init();
    }

    @Test
    @DisplayName("Test aggregate service functionality")
    public void givenListOfServiceUdt_whenAggregateService_thenRepositoryIsCalled(){
        //given
        List<ServiceUdt> services = List.of(DataUtilsCassandra.getVpnServiceOne(),
                DataUtilsCassandra.getVpnServiceTwo(),
                DataUtilsCassandra.getMusicService());
        //when
        serviceService.aggregateService(services);
        //then
        ArgumentCaptor<Iterable<AggregatedServiceStats>> captor = ArgumentCaptor.forClass((Class) Iterable.class);
        verify(serviceRepository, times(1)).saveAll(captor.capture());

        List<AggregatedServiceStats> savedList = new ArrayList<>();
        captor.getValue().forEach(savedList::add);
    }

    @Test
    @DisplayName("Test aggregation part functionality")
    public void givenListOfServiceUdt_whenAggregateService_thenMapStringAggregatedServiceStatsIsReturned(){
        //given
        List<ServiceUdt> servicesForAggregation = List.of(DataUtilsCassandra.getVpnServiceOne(),
                DataUtilsCassandra.getVpnServiceTwo(),
                DataUtilsCassandra.getMusicService());

        //when
        Map<String, AggregatedServiceStats> aggregatedServiceStats = serviceService.aggregationPart(servicesForAggregation);
        Map<String, AggregatedServiceStats> expectedResult = Map.of("MUSICframi.infoCotton Table", DataUtilsService.getMusicServiceTransient(),
                "VPNdickinson.nameSmall Plastic Lamp", DataUtilsService.getVpnServiceTransient());

        //then
        assertThat(aggregatedServiceStats.isEmpty()).isFalse();
        assertThat(aggregatedServiceStats.size()).isEqualTo(2);
        assertThat(aggregatedServiceStats.keySet()).isEqualTo(expectedResult.keySet());
        for(String key : expectedResult.keySet()){
            assertThat(aggregatedServiceStats.get(key).getServiceType()).isEqualTo(expectedResult.get(key).getServiceType());
            assertThat(aggregatedServiceStats.get(key).getName()).isEqualTo(expectedResult.get(key).getName());
            assertThat(aggregatedServiceStats.get(key).getDomain()).isEqualTo(expectedResult.get(key).getDomain());
            assertThat(aggregatedServiceStats.get(key).getTotalInTraffic()).isEqualTo(expectedResult.get(key).getTotalInTraffic());
            assertThat(aggregatedServiceStats.get(key).getTotalOutTraffic()).isEqualTo(expectedResult.get(key).getTotalOutTraffic());
            assertThat(aggregatedServiceStats.get(key).getAvgLatency()).isEqualTo(expectedResult.get(key).getAvgLatency());
            assertThat(aggregatedServiceStats.get(key).getAvgScore()).isEqualTo(expectedResult.get(key).getAvgScore());
            assertThat(aggregatedServiceStats.get(key).getQualityCounts()).isEqualTo(expectedResult.get(key).getQualityCounts());

        }
    }

    @Test
    @DisplayName("Test quality count initializer functionality")
    public void givenServiceQualityUdt_whenQualityCountInitializer_thenMapStringIntegerIsReturned(){
        //given
        ServiceQualityUdt quality = ServiceQualityUdt.GOOD;
        //when
        Map<String, Integer> qualityCount = serviceService.qualityCountInitializer(quality);
        //then
        assertThat(qualityCount.isEmpty()).isFalse();
        assertThat(qualityCount.size()).isEqualTo(3);
        assertThat(qualityCount.get("GOOD")).isEqualTo(1);
        assertThat(qualityCount.get("WARNING")).isEqualTo(0);
        assertThat(qualityCount.get("BAD")).isEqualTo(0);
    }

    @Test
    @DisplayName("Test calculate key functionality")
    public void givenServiceUdt_whenCalculateKey_thenStringIsReturned(){
        //given
        ServiceUdt service = DataUtilsCassandra.getVpnServiceOne();
        //when
        String key = serviceService.calculateKey(service);
        String expected = "VPNdickinson.nameSmall Plastic Lamp";
        //then
        assertThat(key).isNotNull();
        assertThat(key).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test count services functionality")
    public void givenListOfServiceUdt_whenCountServices_thenMapOfStringIntegerIsReturned(){
        //given
        List<ServiceUdt> services = List.of(DataUtilsCassandra.getVpnServiceOne(),
                DataUtilsCassandra.getVpnServiceTwo(),
                DataUtilsCassandra.getMusicService());
        //when
        Map<String, Integer> countServices = serviceService.countServices(services);
        Map<String, Integer> expected = Map.of("MUSICframi.infoCotton Table", 1,
                "VPNdickinson.nameSmall Plastic Lamp", 2);
        //then
        assertThat(countServices.isEmpty()).isFalse();
        assertThat(countServices.keySet()).isEqualTo(expected.keySet());
        for(String key : countServices.keySet()){
            assertThat(countServices.get(key)).isEqualTo(expected.get(key));
        }
    }

    @Test
    @DisplayName("Test unite two services functionality")
    public void givenTwoAggregatedServiceStats_whenUniteTwoServices_thenAggregatedServiceStatsIsReturned(){
        //given
        AggregatedServiceStats service1 = DataUtilsService.getVpnServiceTransient();
        AggregatedServiceStats service2 = DataUtilsService.getVpnServiceForUniteTransient();
        //when
        AggregatedServiceStats unitedService = serviceService.uniteTwoService(service1, service2);
        AggregatedServiceStats expected = DataUtilsService.getVpnServiceUnitedTransient();
        //then
        assertThat(unitedService).isNotNull();
        assertThat(unitedService.getServiceType()).isEqualTo(expected.getServiceType());
        assertThat(unitedService.getName()).isEqualTo(expected.getName());
        assertThat(unitedService.getDomain()).isEqualTo(expected.getDomain());
        assertThat(unitedService.getAvgLatency()).isEqualTo(expected.getAvgLatency());
        assertThat(unitedService.getAvgScore()).isEqualTo(expected.getAvgScore());
        assertThat(unitedService.getTotalInTraffic()).isEqualTo(expected.getTotalInTraffic());
        assertThat(unitedService.getTotalOutTraffic()).isEqualTo(expected.getTotalOutTraffic());
        assertThat(unitedService.getQualityCounts()).isEqualTo(expected.getQualityCounts());
    }
}
