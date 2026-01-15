package io.leantech.telemetryaggregator.service.postgres;

import io.leantech.telemetryaggregator.model.cassandra.udt.DeviceUdt;
import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceUdt;
import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import io.leantech.telemetryaggregator.repository.postgres.DeviceRepository;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final MeterRegistry registry;
    private DistributionSummary recordsSaved;
    private final DeviceRepository deviceRepository;

    private Map<String, AggregatedDeviceStats> aggregatedDeviceStatsMap;

    @PostConstruct
    public void init(){
        this.recordsSaved = DistributionSummary.builder("postgres.aggregated.devices.stats.saved")
                .description("Number of records saved to aggregated-device-stats table")
                .tag("table", "aggregated-device-stats")
                .tag("component", "device-service")
                .register(registry);
    }

    /* --- Key for devices is 'ip' --- */
    public void aggregateDevice(List<DeviceUdt> devices){

        log.info("Start devices aggregation");
        Map<String, AggregatedDeviceStats> aggregatedDevice = aggregationPart(devices);
        log.info("Saving aggregated devices stats");
        deviceRepository.saveAll(aggregatedDevice.values());
        recordsSaved.record(aggregatedDevice.size());
        log.info("Aggregated device stats saved successfully");
    }

    public Map<String, AggregatedDeviceStats> aggregationPart(List<DeviceUdt> devices){
        Map<String, Integer> countDevices = countDevices(devices);
        //unique services
        Map<String, Set<ServiceUdt>> uniqueServices = countUniqueServices(devices);

        Map<String, AggregatedDeviceStats> aggregateResult = devices.stream().collect(Collectors.toMap(this::calculateKey,
                device -> new AggregatedDeviceStats(Instant.now(),
                        calculateKey(device),
                        device.getFingerprint().getBrand(),
                        device.getFingerprint().getModel(),
                        device.getFingerprint().getOsName(),
                        device.getFingerprint().getMac(),
                        (double) device.getCpuUsage(),
                        (double) device.getMemoryUsage(),
                        device.getServices().size()),
                this::uniteTwoDevices));

        for(String key : aggregateResult.keySet()){
            aggregateResult.get(key).setMemoryUsageAvg(aggregateResult.get(key).getMemoryUsageAvg() / countDevices.get(key));
            aggregateResult.get(key).setCpuUsageAvg(aggregateResult.get(key).getCpuUsageAvg() / countDevices.get(key));
            aggregateResult.get(key).setServicesCount(uniqueServices.get(key).size());
        }

        return aggregateResult;
    }

    public String calculateKey(DeviceUdt device){
        return device.getIp().getFirst();
    }

    public AggregatedDeviceStats uniteTwoDevices(AggregatedDeviceStats device1, AggregatedDeviceStats device2){
        return AggregatedDeviceStats.builder()
                .timestamp(Instant.now())
                .ip(device1.getIp())
                .brand(device1.getBrand())
                .model(device1.getModel())
                .mac(device1.getMac())
                .osName(device1.getOsName())
                .cpuUsageAvg(device1.getCpuUsageAvg() + device2.getCpuUsageAvg())
                .memoryUsageAvg(device1.getMemoryUsageAvg() + device2.getMemoryUsageAvg())
                .servicesCount(device1.getServicesCount())
                .build();
    }

    public Map<String, Set<ServiceUdt>> countUniqueServices(List<DeviceUdt> devices){
        Map<String, Set<ServiceUdt>> result = devices.stream().collect(Collectors.toMap(this::calculateKey,
                device -> new HashSet<>(device.getServices().values()),
                (set1, set2) -> {set1.addAll(set2);
                                    return set1;}));
        return result;
    }

    public Map<String, Integer> countDevices(List<DeviceUdt> devices){
        Map<String, Integer> result = devices.stream().collect(Collectors.toMap(this::calculateKey,
                _ -> 1,
                Integer::sum));
        return result;
    }
}
