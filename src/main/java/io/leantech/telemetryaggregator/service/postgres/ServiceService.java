package io.leantech.telemetryaggregator.service.postgres;

import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceQualityUdt;
import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceUdt;
import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;
import io.leantech.telemetryaggregator.repository.postgres.ServiceRepository;
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
public class ServiceService {

    private final MeterRegistry registry;
    private DistributionSummary recordsSaved;
    private final ServiceRepository serviceRepository;

    private Map<String, AggregatedServiceStats> aggregatedServiceStatsMap;

    @PostConstruct
    public void init(){
        this.recordsSaved = DistributionSummary.builder("postgres.aggregated.service.stats.saved")
                .description("Number of records saved to aggregated-service-stats table")
                .tag("table", "aggregated-service-stats")
                .tag("component", "service-service")
                .register(registry);
    }

    /* --- Key for service is service_type+domain+name --- */
    public void aggregateService(List<ServiceUdt> services){

        //result
        log.info("Start services aggregation");
        Map<String, AggregatedServiceStats> aggregatedService = aggregationPart(services);
        log.info("Saving aggregated services stats");
        serviceRepository.saveAll(aggregatedService.values());
        recordsSaved.record(aggregatedService.size());
        log.info("Aggregated service stats saved successfully");

    }

    public Map<String, AggregatedServiceStats> aggregationPart(List<ServiceUdt> services){
        Map<String, Integer> countServices = countServices(services);

        Map<String, AggregatedServiceStats> aggregatedResult = services.stream().collect(Collectors.toMap(this::calculateKey,
                service -> new AggregatedServiceStats(Instant.now(),
                        service.getType().name(),
                        service.getDomain(),
                        service.getName(),
                        service.getLatency(),
                        service.getScore(),
                        service.getInTraffic(),
                        service.getOutTraffic(),
                        qualityCountInitializer(service.getServiceQuality())),
                this::uniteTwoService));

        for(String key : aggregatedResult.keySet()){
            aggregatedResult.get(key).setAvgLatency(aggregatedResult.get(key).getAvgLatency() / countServices.get(key));
            aggregatedResult.get(key).setAvgScore(aggregatedResult.get(key).getAvgScore() / countServices.get(key));

        }

        return aggregatedResult;
    }

    public Map<String, Integer> qualityCountInitializer(ServiceQualityUdt quality){
        Map<String, Integer> qualityCount = new LinkedHashMap<>();
        qualityCount.put("GOOD", 0);
        qualityCount.put("WARNING", 0);
        qualityCount.put("BAD", 0);

        qualityCount.put(quality.name(), 1);
        return qualityCount;
    }

    public String calculateKey(ServiceUdt service){
        return service.getType() + service.getDomain() + service.getName();
    }

    public Map<String, Integer> countServices(List<ServiceUdt> services){
        Map<String, Integer> result = services.stream().collect(Collectors.toMap(this::calculateKey,
                service -> 1,
                Integer::sum));
        return result;
    }

    public AggregatedServiceStats uniteTwoService(AggregatedServiceStats service1, AggregatedServiceStats service2){
        AggregatedServiceStats result = AggregatedServiceStats.builder()
                .avgLatency(service1.getAvgLatency() + service2.getAvgLatency())
                .avgScore(service1.getAvgScore() + service2.getAvgScore())
                .domain(service1.getDomain())
                .name(service1.getName())
                .serviceType(service1.getServiceType())
                .timestamp(Instant.now())
                .totalInTraffic(service1.getTotalInTraffic() + service2.getTotalInTraffic())
                .totalOutTraffic(service1.getTotalOutTraffic() + service2.getTotalOutTraffic())
                .qualityCounts(Map.of("GOOD", service1.getQualityCounts().get("GOOD") + service2.getQualityCounts().get("GOOD"),
                        "WARNING", service1.getQualityCounts().get("WARNING") + service2.getQualityCounts().get("WARNING"),
                        "BAD", service1.getQualityCounts().get("BAD") + service2.getQualityCounts().get("BAD")))
                .build();
        return result;
    }
}
