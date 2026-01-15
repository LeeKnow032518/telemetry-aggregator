package io.leantech.telemetryaggregator.utils.postgres;

import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;

import java.time.Instant;
import java.util.Map;

public class DataUtilsService {

    public static AggregatedServiceStats getMusicServiceTransient(){
        return AggregatedServiceStats.builder()
                .timestamp(Instant.now())
                .serviceType("MUSIC")
                .domain("frami.info")
                .name("Cotton Table")
                .totalInTraffic(15)
                .totalOutTraffic(6)
                .qualityCounts(Map.of("GOOD", 0,
                        "WARNING", 1,
                        "BAD", 0))
                .avgLatency(10.0)
                .avgScore(5.0)
                .build();
    }

    public static AggregatedServiceStats getVpnServiceTransient(){
        return AggregatedServiceStats.builder()
                .timestamp(Instant.now())
                .serviceType("VPN")
                .domain("dickinson.name")
                .name("Small Plastic Lamp")
                .totalInTraffic(60)
                .totalOutTraffic(30)
                .qualityCounts(Map.of("GOOD", 1,
                        "WARNING", 1,
                        "BAD", 0))
                .avgLatency(7.5)
                .avgScore(15.0)
                .build();
    }

    public static AggregatedServiceStats getVpnServiceForUniteTransient(){
        return AggregatedServiceStats.builder()
                .timestamp(Instant.now())
                .serviceType("VPN")
                .domain("dickinson.name")
                .name("Small Plastic Lamp")
                .totalInTraffic(40)
                .totalOutTraffic(40)
                .qualityCounts(Map.of("GOOD", 1,
                        "WARNING", 1,
                        "BAD", 1))
                .avgLatency(2.4)
                .avgScore(0.5)
                .build();
    }

    public static AggregatedServiceStats getVpnServiceUnitedTransient(){
        return AggregatedServiceStats.builder()
                .timestamp(Instant.now())
                .serviceType("VPN")
                .domain("dickinson.name")
                .name("Small Plastic Lamp")
                .totalInTraffic(100)
                .totalOutTraffic(70)
                .qualityCounts(Map.of("GOOD", 2,
                        "WARNING", 2,
                        "BAD", 1))
                .avgLatency(9.9)
                .avgScore(15.5)
                .build();
    }
}
