package io.leantech.telemetryaggregator.utils.cassandra;

import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import io.leantech.telemetryaggregator.model.cassandra.udt.*;
import jnr.ffi.annotations.In;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public class DataUtilsCassandra {

    /* --- Telemetry Testing Entities --- */

    //TelemetryEntity same hostname + domain
    public static TelemetryEntity getTelemetryEventFirst(){
        String eventHour = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));
        return TelemetryEntity.builder()
                .eventTimestamp(Instant.now())
                .eventHour(eventHour)
                .eventId(UUID.randomUUID())
                .devices(Map.of("one", getDeviceFirst()))
                .discoveryStatus(getDiscoveryStatusHermanOne())
                .build();
    }

    //TelemetryEntity same hostname + domain
    public static TelemetryEntity getTelemetryEventSecond(){
        String eventHour = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));
        return TelemetryEntity.builder()
                .eventTimestamp(Instant.now())
                .eventHour(eventHour)
                .eventId(UUID.randomUUID())
                .devices(Map.of("one", getDeviceSecond()))
                .discoveryStatus(getDiscoveryStatusHermanTwo())
                .build();
    }

    //TelemetryEntity different hostname + domain
    public static TelemetryEntity getTelemetryEventThird(){
        String eventHour = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));
        return TelemetryEntity.builder()
                .eventTimestamp(Instant.now())
                .eventHour(eventHour)
                .eventId(UUID.randomUUID())
                .devices(Map.of("one", getDeviceDifferent()))
                .discoveryStatus(getDiscoveryStatusWeissant())
                .build();
    }

    /* --- Discovery status Testing Entities --- */

    public static DiscoveryStatusUdt getDiscoveryStatusHermanOne(){
        return DiscoveryStatusUdt.builder()
                .agentVersion("2.9.0")
                .connectionDomain("herman.io")
                .inBitrate(9200)
                .outBitrate(9000)
                .inTraffic(100)
                .outTraffic(50)
                .inClassifiedTraffic(50)
                .outClassifiedTraffic(25)
                .pingToSystem(24).build();
    }

    public static DiscoveryStatusUdt getDiscoveryStatusHermanTwo(){
        return DiscoveryStatusUdt.builder()
                .agentVersion("2.9.0")
                .connectionDomain("herman.io")
                .inBitrate(10000)
                .outBitrate(7000)
                .inTraffic(50)
                .outTraffic(25)
                .inClassifiedTraffic(90)
                .outClassifiedTraffic(10)
                .pingToSystem(90)
                .build();
    }

    public static DiscoveryStatusUdt getDiscoveryStatusWeissant(){
        return DiscoveryStatusUdt.builder()
                .agentVersion("1.0.0")
                .connectionDomain("weissant.io")
                .inBitrate(9000)
                .outBitrate(7000)
                .inTraffic(70)
                .outTraffic(10)
                .inClassifiedTraffic(100)
                .outClassifiedTraffic(80)
                .pingToSystem(80)
                .build();

    }

    /* --- Device Testing Entities --- */

    //device one
    public static DeviceUdt getDeviceFirst(){
        return DeviceUdt.builder()
                .ip(List.of("170.183.48.14"))
                .cpuUsage(50)
                .memoryUsage(100)
                .fingerprint(getFingerprintOne())
                .services(Map.of("one", getVpnServiceOne())).build();
    }
    //device two (same ip, different cpu and memory usage)

    public static DeviceUdt getDeviceSecond(){
        return DeviceUdt.builder()
                .ip(List.of("170.183.48.14"))
                .cpuUsage(20)
                .memoryUsage(20)
                .fingerprint(getFingerprintOne())
                .services(Map.of("one", getVpnServiceTwo())).build();
    }

    //device three (different ip + different fingerprint)

    public static DeviceUdt getDeviceDifferent(){
        return DeviceUdt.builder()
                .ip(List.of("225.148.195.149"))
                .cpuUsage(50)
                .memoryUsage(80)
                .fingerprint(getFingerprintTwo())
                .services(Map.of("one", getMusicService()))
                .build();
    }

    //fingerprint first and second device
    public static FingerprintUdt getFingerprintOne(){
        return FingerprintUdt.builder()
                .brand("Plum")
                .hostname("router-967")
                .model("iPhone SE")
                .mac("mac1")
                .osName("Catalina")
                .type("Firefox OS").build();
    }

    //fingerprint third device
    public static FingerprintUdt getFingerprintTwo(){
        return FingerprintUdt.builder()
                .brand("Plum")
                .hostname("router-100")
                .model("iPhone 5")
                .mac("mac2")
                .osName("Ubuntu")
                .type("Windows 10").build();
    }

    /* --- Services Test Entities --- */

    //service VPN one
    public static ServiceUdt getVpnServiceOne(){
        return ServiceUdt.builder()
                .type(ServiceTypeUdt.VPN)
                .domain("dickinson.name")
                .name("Small Plastic Lamp")
                .startTime(0L)
                .inTraffic(50)
                .outTraffic(25)
                .score(10)
                .latency(10)
                .serviceQuality(ServiceQualityUdt.GOOD)
                .build();
    }

    //service VPN two (same service_type, domain, name, different everything else)

    public static ServiceUdt getVpnServiceTwo(){
        return ServiceUdt.builder()
                .type(ServiceTypeUdt.VPN)
                .domain("dickinson.name")
                .name("Small Plastic Lamp")
                .startTime(0L)
                .inTraffic(10)
                .outTraffic(5)
                .score(20)
                .latency(5)
                .serviceQuality(ServiceQualityUdt.WARNING)
                .build();
    }

    //service MUSIC three

    public static ServiceUdt getMusicService(){
        return ServiceUdt.builder()
                .type(ServiceTypeUdt.MUSIC)
                .domain("frami.info")
                .name("Cotton Table")
                .startTime(0L)
                .inTraffic(15)
                .outTraffic(6)
                .score(5)
                .latency(10)
                .serviceQuality(ServiceQualityUdt.WARNING)
                .build();
    }

    /* ---  --- */
}
