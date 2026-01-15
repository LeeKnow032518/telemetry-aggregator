package io.leantech.telemetryaggregator.model.postgres;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("aggregated_router_stats")
public class AggregatedRouterStats extends AggregateResult{

    @Id
    private UUID id;

    @Column("timestamp")
    @NonNull
    private Instant timestamp;

    @Column("domain")
    @NonNull
    private String domain;

    @Column("hostname")
    @NonNull
    private String hostname;

    @Column("total_in_traffic")
    private long totalInTraffic;

    @Column("total_out_traffic")
    private long totalOutTraffic;

    @Column("avg_score")
    private double avgScore;

    @Column("avg_latency")
    private double avgLatency;

    @Column("unique_devices")
    private int uniqueDevices;

    public AggregatedRouterStats(@NonNull Instant timestamp, @NonNull String domain, @NonNull String hostname, long totalInTraffic, long totalOutTraffic, double avgScore, double avgLatency, int uniqueDevices) {
        this.timestamp = timestamp;
        this.domain = domain;
        this.hostname = hostname;
        this.totalInTraffic = totalInTraffic;
        this.totalOutTraffic = totalOutTraffic;
        this.avgScore = avgScore;
        this.avgLatency = avgLatency;
        this.uniqueDevices = uniqueDevices;
    }

}

