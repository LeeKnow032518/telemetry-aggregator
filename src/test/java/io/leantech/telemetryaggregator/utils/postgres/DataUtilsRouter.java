package io.leantech.telemetryaggregator.utils.postgres;

import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;

import java.time.Instant;

public class DataUtilsRouter {

    public static AggregatedRouterStats getRouterHermanTransient(){
        return AggregatedRouterStats.builder()
                .timestamp(Instant.now())
                .domain("herman.io")
                .hostname("router-967")
                .totalInTraffic(150)
                .totalOutTraffic(75)
                .avgLatency(7.5)
                .avgScore(15)
                .uniqueDevices(1)
                .build();
    }

    public static AggregatedRouterStats getRouterWeissantTransient(){
        return AggregatedRouterStats.builder()
                .timestamp(Instant.now())
                .domain("weissant.io")
                .hostname("router-100")
                .totalInTraffic(70)
                .totalOutTraffic(10)
                .avgLatency(10)
                .avgScore(5)
                .uniqueDevices(1)
                .build();
    }

    public static AggregatedRouterStats getRouterWeissantForUniteTransient(){
        return AggregatedRouterStats.builder()
                .timestamp(Instant.now())
                .domain("weissant.io")
                .hostname("router-100")
                .totalInTraffic(30)
                .totalOutTraffic(40)
                .avgLatency(5)
                .avgScore(5)
                .uniqueDevices(1)
                .build();
    }

    public static AggregatedRouterStats getRouterWeissantUnitedTransient(){
        return AggregatedRouterStats.builder()
                .timestamp(Instant.now())
                .domain("weissant.io")
                .hostname("router-100")
                .totalInTraffic(100)
                .totalOutTraffic(50)
                .avgLatency(15)
                .avgScore(10)
                .uniqueDevices(1)
                .build();
    }
}
