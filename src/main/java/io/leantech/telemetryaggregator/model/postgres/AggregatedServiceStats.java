package io.leantech.telemetryaggregator.model.postgres;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("aggregated_service_stats")
public class AggregatedServiceStats extends AggregateResult{

    @Column("avg_latency")
    private double avgLatency;

    @Column("avg_score")
    private double avgScore;

    @Column("domain")
    private String domain;

    @Id
    @PrimaryKey
    private UUID id;

    @Column("name")
    private String name;

    private Map<String, Integer> qualityCounts;

    @Column("quality_counts")
    private String qualityCountsString;

    @Column("service_type")
    private String serviceType;

    @Column("timestamp")
    private Instant timestamp;

    @Column("total_in_traffic")
    private long totalInTraffic;

    @Column("total_out_traffic")
    private long totalOutTraffic;

    public AggregatedServiceStats(Instant timestamp, String serviceType, String domain, String name, double avgLatency, double avgScore, long totalInTraffic, long totalOutTraffic, Map<String, Integer> qualityCounts) {
        this.timestamp = timestamp;
        this.serviceType = serviceType;
        this.domain = domain;
        this.name = name;
        this.avgLatency = avgLatency;
        this.avgScore = avgScore;
        this.totalInTraffic = totalInTraffic;
        this.totalOutTraffic = totalOutTraffic;
        this.qualityCounts = qualityCounts;
    }


}

