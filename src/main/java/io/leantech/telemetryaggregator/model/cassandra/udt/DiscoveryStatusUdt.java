package io.leantech.telemetryaggregator.model.cassandra.udt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@UserDefinedType("discovery_status")
public class DiscoveryStatusUdt {

    @Column("agent_version")
    private String agentVersion;

    @Column("connection_domain")
    private String connectionDomain;

    @Column("in_bitrate")
    private long inBitrate;

    @Column("out_bitrate")
    private long outBitrate;

    @Column("in_traffic")
    private long inTraffic;

    @Column("out_traffic")
    private long outTraffic;

    @Column("in_classified_traffic")
    private long inClassifiedTraffic;

    @Column("out_classified_traffic")
    private long outClassifiedTraffic;

    @Column("ping_to_system")
    private int pingToSystem;

}
