package io.leantech.telemetryaggregator.model.cassandra.udt;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.Objects;

@EqualsAndHashCode(of = {"type", "name", "domain"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@UserDefinedType("service")
public class ServiceUdt {

    @Column("type")
    private ServiceTypeUdt type;

    @Column("domain")
    private String domain;

    @Column("name")
    private String name;

    @Column("start_time")
    private long startTime;

    @Column("in_traffic")
    private long inTraffic;

    @Column("out_traffic")
    private long outTraffic;

    @Column("score")
    private int score;

    @Column("latency")
    private int latency;

    @Column("service_quality")
    private ServiceQualityUdt serviceQuality;

}
