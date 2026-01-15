package io.leantech.telemetryaggregator.service.postgres;

import io.leantech.telemetryaggregator.model.cassandra.udt.DeviceUdt;
import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceUdt;
import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import io.leantech.telemetryaggregator.repository.postgres.DeviceRepository;
import io.leantech.telemetryaggregator.utils.cassandra.DataUtilsCassandra;
import io.leantech.telemetryaggregator.utils.postgres.DataUtilsDevice;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTests {

    @Mock
    private DeviceRepository deviceRepository;

    private final MeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, Clock.SYSTEM);

    @Mock
    private DistributionSummary recordsSaved;

    @InjectMocks
    private DeviceService deviceService;

    @BeforeEach
    public void setUp(){
        deviceService = new DeviceService(registry, deviceRepository);
        deviceService.init();
    }

    @Test
    @DisplayName("Test aggregate device functionality")
    public void givenListOfDeviceUdt_whenAggregateDevice_thenRepositoryIsCalled(){
        //given
        List<DeviceUdt> devices =  List.of(DataUtilsCassandra.getDeviceFirst(),
                DataUtilsCassandra.getDeviceSecond(), DataUtilsCassandra.getDeviceDifferent());
        //when
        deviceService.aggregateDevice(devices);
        //then
        ArgumentCaptor<Iterable<AggregatedDeviceStats>> captor = ArgumentCaptor.forClass((Class) Iterable.class);
        verify(deviceRepository, times(1)).saveAll(captor.capture());

        List<AggregatedDeviceStats> savedList = new ArrayList<>();
        captor.getValue().forEach(savedList::add);
    }

    @Test
    @DisplayName("Test aggregation part functionality")
    public void givenListOfDevicesUdt_whenAggregationPart_thenMapStringAggregatedDeviceStatsIsReturned(){
        //given
        List<DeviceUdt> devicesForAggregation = List.of(DataUtilsCassandra.getDeviceFirst(),
                DataUtilsCassandra.getDeviceSecond(), DataUtilsCassandra.getDeviceDifferent());
        //when
        Map<String, AggregatedDeviceStats> aggregatedDeviceStats = deviceService.aggregationPart(devicesForAggregation);
        Map<String, AggregatedDeviceStats> expectedResult = Map.of("170.183.48.14", DataUtilsDevice.getDevicePlumTransient(),
                "225.148.195.149", DataUtilsDevice.getDevicePlumSecondTransient());

        //then
        assertThat(aggregatedDeviceStats.isEmpty()).isFalse();
        assertThat(aggregatedDeviceStats.size()).isEqualTo(2);
        assertThat(aggregatedDeviceStats.keySet()).isEqualTo(expectedResult.keySet());
        for(String key : aggregatedDeviceStats.keySet()){
            assertThat(aggregatedDeviceStats.get(key).getBrand()).isEqualTo(expectedResult.get(key).getBrand());
            assertThat(aggregatedDeviceStats.get(key).getModel()).isEqualTo(expectedResult.get(key).getModel());
            assertThat(aggregatedDeviceStats.get(key).getMac()).isEqualTo(expectedResult.get(key).getMac());
            assertThat(aggregatedDeviceStats.get(key).getOsName()).isEqualTo(expectedResult.get(key).getOsName());
            assertThat(aggregatedDeviceStats.get(key).getCpuUsageAvg()).isEqualTo(expectedResult.get(key).getCpuUsageAvg());
            assertThat(aggregatedDeviceStats.get(key).getMemoryUsageAvg()).isEqualTo(expectedResult.get(key).getMemoryUsageAvg());
            assertThat(aggregatedDeviceStats.get(key).getServicesCount()).isEqualTo(expectedResult.get(key).getServicesCount());
        }
    }

    @Test
    @DisplayName("Test calculate key functionality")
    public void givenDeviceUdtObject_whenCalculateKey_thenStringIsReturned(){
        //given
        DeviceUdt deviceForTest = DataUtilsCassandra.getDeviceDifferent();
        //when
        String key = deviceService.calculateKey(deviceForTest);
        //then
        assertThat(key).isNotNull();
        assertThat(key).isEqualTo(deviceForTest.getIp().getFirst());
    }

    @Test
    @DisplayName("Test unite two devices functionality")
    public void givenTwoAggregatedDeviceStatsObjects_whenUniteTwoDevices_thenOneAggregatedDeviceStatsIsReturned(){
        //given
        AggregatedDeviceStats device1 = DataUtilsDevice.getDevicePlumSecondTransient();
        AggregatedDeviceStats device2 = DataUtilsDevice.getDevicePlumSecondForUniteTransient();
        //when
        AggregatedDeviceStats deviceUnited = deviceService.uniteTwoDevices(device1, device2);
        AggregatedDeviceStats expected = DataUtilsDevice.getDevicePlumSecondUnitedTransient();
        //then
        assertThat(deviceUnited).isNotNull();
        assertThat(deviceUnited.getIp()).isEqualTo(expected.getIp());
        assertThat(deviceUnited.getBrand()).isEqualTo(expected.getBrand());
        assertThat(deviceUnited.getModel()).isEqualTo(expected.getModel());
        assertThat(deviceUnited.getOsName()).isEqualTo(expected.getOsName());
        assertThat(deviceUnited.getCpuUsageAvg()).isEqualTo(expected.getCpuUsageAvg());
        assertThat(deviceUnited.getMemoryUsageAvg()).isEqualTo(expected.getMemoryUsageAvg());
        assertThat(deviceUnited.getServicesCount()).isEqualTo(expected.getServicesCount());
    }

    @Test
    @DisplayName("Test count devices functionality")
    public void givenListOfDeviceUdt_whenCountDevices_thenMapOfStringIntegerIsReturned(){
        //given
        List<DeviceUdt> devices = List.of(DataUtilsCassandra.getDeviceFirst(),
                DataUtilsCassandra.getDeviceSecond(), DataUtilsCassandra.getDeviceDifferent());
        //when
        Map<String, Integer> count = deviceService.countDevices(devices);
        Map<String, Integer> expected = Map.of("170.183.48.14", 2,
                                                "225.148.195.149", 1);
        //then
        assertThat(count.isEmpty()).isFalse();
        assertThat(count.keySet()).isEqualTo(expected.keySet());
        for(String key : count.keySet()){
            assertThat(count.get(key)).isEqualTo(expected.get(key));
        }
    }

    @Test
    @DisplayName("Test count unique services functionality")
    public void givenListOfDeviceUdt_whenCountUniqueServices_thenMapOfStringSetOfServiceUdtIsReturned(){
        //given
        List<DeviceUdt> devices = List.of(DataUtilsCassandra.getDeviceFirst(),
                DataUtilsCassandra.getDeviceSecond(), DataUtilsCassandra.getDeviceDifferent());
        //when
        Map<String, Set<ServiceUdt>> uniqueServices = deviceService.countUniqueServices(devices);
        Map<String, Set<ServiceUdt>> expected = Map.of("170.183.48.14", Set.of(DataUtilsCassandra.getVpnServiceTwo()),
                                                    "225.148.195.149", Set.of(DataUtilsCassandra.getMusicService()));
        //then
        assertThat(uniqueServices.isEmpty()).isFalse();
        assertThat(uniqueServices.keySet().size()).isEqualTo(2);
        for(String key : uniqueServices.keySet()){
            assertThat(uniqueServices.get(key)).isEqualTo(expected.get(key));
        }
    }
}
