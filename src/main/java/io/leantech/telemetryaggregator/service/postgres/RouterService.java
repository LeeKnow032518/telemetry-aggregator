package io.leantech.telemetryaggregator.service.postgres;


import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import io.leantech.telemetryaggregator.model.cassandra.udt.DeviceUdt;
import io.leantech.telemetryaggregator.model.cassandra.udt.ServiceUdt;
import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import io.leantech.telemetryaggregator.repository.postgres.RouterRepository;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouterService {

    private final MeterRegistry registry;
    private DistributionSummary recordsSaved;
    private final RouterRepository routerRepository;

    private Map<String, AggregatedRouterStats> aggregatedRouterStatsMap;

    @PostConstruct
    public void init(){
        this.recordsSaved = DistributionSummary.builder("postgres.aggregated.router.stats.saved")
                .description("Number of records saved to aggregated-router-stats table")
                .tag("table", "aggregated-router-stats")
                .tag("component", "router-service")
                .register(registry);
    }

    /* --- Key for routers is 'hostname + domain' --- */
    public void aggregateRouter(List<TelemetryEntity> routers){

        //result
        log.info("Start routers aggregation");
        Map<String, AggregatedRouterStats> aggregatedRouter = aggregationPart(routers);
        log.info("Saving aggregated routers stats");
        routerRepository.saveAll(aggregatedRouter.values());
        recordsSaved.record(aggregatedRouter.size());
        log.info("Aggregated routers stats saved successfully");
    }

    public Map<String, AggregatedRouterStats> aggregationPart(List<TelemetryEntity> routers){
        Map<String, Integer> countServices = countServices(routers);
        Map<String, Set<DeviceUdt>> uniqueDevices = countUniqueDevices(routers);

        Map<String, AggregatedRouterStats> aggregatedResult =
                routers.stream().collect(toMap(this::calculateKey,
                        router -> new AggregatedRouterStats(Instant.now(),
                                router.getDiscoveryStatus().getConnectionDomain(),
                                router.getDevices().values().stream().iterator().next().getFingerprint().getHostname(),
                                router.getDiscoveryStatus().getInTraffic(),
                                router.getDiscoveryStatus().getOutTraffic(),
                                (double) router.getDevices().values().stream()
                                        .map(device -> device.getServices().values().stream().map(ServiceUdt::getScore).toList().stream()
                                                .reduce(Integer::sum).get()).reduce(Integer::sum).get(),
                                (double) router.getDevices().values().stream()
                                        .map(device -> device.getServices().values().stream().map(ServiceUdt::getLatency).toList().stream()
                                                .reduce(Integer::sum).get()).reduce(Integer::sum).get(),
                                router.getDevices().size()),
                        this::uniteTwoRouters));

        for(String key : aggregatedResult.keySet()){
            aggregatedResult.get(key).setAvgScore(aggregatedResult.get(key).getAvgScore() / countServices.get(key));
            aggregatedResult.get(key).setAvgLatency(aggregatedResult.get(key).getAvgLatency() / countServices.get(key));
            aggregatedResult.get(key).setUniqueDevices(uniqueDevices.get(key).size());
        }
        return aggregatedResult;
    }

    public Map<String, Integer> countServices(List<TelemetryEntity> routers){
        Map<String, Integer> result = routers.stream().collect(toMap(this::calculateKey,
                router -> router.getDevices().values().stream()
                        .map(device -> device.getServices().values().size()).reduce(Integer::sum).stream().reduce(Integer::sum).get(),
                Integer::sum));
        return result;
    }

    public String calculateKey(TelemetryEntity router){
        return router.getDevices().values().stream().iterator().next().getFingerprint().getHostname()
                +router.getDiscoveryStatus().getConnectionDomain();
    }

    public Map<String, Set<DeviceUdt>> countUniqueDevices(List<TelemetryEntity> routers){
        Map<String, Set<DeviceUdt>> result = routers.stream().collect(toMap(this::calculateKey,
                router -> new HashSet<>(router.getDevices().values()),
                (set1, set2) -> {set1.addAll(set2);
                                    return set1;}));
        return result;
    }

    public AggregatedRouterStats uniteTwoRouters(AggregatedRouterStats router1, AggregatedRouterStats router2){
        AggregatedRouterStats resultRouter = new AggregatedRouterStats();
        resultRouter.setTimestamp(Instant.now());
        resultRouter.setDomain(router1.getDomain());
        resultRouter.setHostname(router1.getHostname());
        resultRouter.setTotalInTraffic(router1.getTotalInTraffic() + router2.getTotalInTraffic());
        resultRouter.setTotalOutTraffic(router1.getTotalOutTraffic() + router2.getTotalOutTraffic());
        resultRouter.setAvgScore(router1.getAvgScore() + router2.getAvgScore());
        resultRouter.setAvgLatency(router1.getAvgLatency() + router2.getAvgLatency());

        return resultRouter;
    }
}
