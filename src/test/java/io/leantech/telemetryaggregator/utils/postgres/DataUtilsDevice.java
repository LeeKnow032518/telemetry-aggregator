package io.leantech.telemetryaggregator.utils.postgres;

import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;

import java.time.Instant;

public class DataUtilsDevice {

    public static AggregatedDeviceStats getDevicePlumTransient(){
        return AggregatedDeviceStats.builder()
                .timestamp(Instant.now())
                .ip("170.183.48.14")
                .brand("Plum")
                .model("iPhone SE")
                .mac("mac1")
                .osName("Catalina")
                .cpuUsageAvg(35)
                .memoryUsageAvg(60)
                .servicesCount(1).build();
    }

    public static AggregatedDeviceStats getDevicePlumSecondTransient(){
        return AggregatedDeviceStats.builder()
                .timestamp(Instant.now())
                .ip("225.148.195.149")
                .brand("Plum")
                .model("iPhone 5")
                .mac("mac2")
                .osName("Ubuntu")
                .cpuUsageAvg(50)
                .memoryUsageAvg(80)
                .servicesCount(1)
                .build();
    }
    public static AggregatedDeviceStats getDevicePlumSecondForUniteTransient(){
        return AggregatedDeviceStats.builder()
                .timestamp(Instant.now())
                .ip("225.148.195.149")
                .brand("Plum")
                .model("iPhone 5")
                .mac("mac2")
                .osName("Ubuntu")
                .cpuUsageAvg(10)
                .memoryUsageAvg(20)
                .servicesCount(1)
                .build();
    }

    public static AggregatedDeviceStats getDevicePlumSecondUnitedTransient(){
        return AggregatedDeviceStats.builder()
                .timestamp(Instant.now())
                .ip("225.148.195.149")
                .brand("Plum")
                .model("iPhone 5")
                .mac("mac2")
                .osName("Ubuntu")
                .cpuUsageAvg(60)
                .memoryUsageAvg(100)
                .servicesCount(1)
                .build();
    }
}
