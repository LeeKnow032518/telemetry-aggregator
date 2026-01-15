package io.leantech.telemetryaggregator.model.cassandra;

import io.leantech.telemetryaggregator.model.cassandra.udt.DeviceUdt;
import io.leantech.telemetryaggregator.model.cassandra.udt.DiscoveryStatusUdt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("telemetry_event")
public class TelemetryEntity {

    @PrimaryKey
    @Column("event_id")
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID eventId;

    @Column("discovery_status")
    @CassandraType(type = CassandraType.Name.UDT, userTypeName = "discovery_status")
    private DiscoveryStatusUdt discoveryStatus;

    @Column("event_timestamp")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Instant eventTimestamp;

    @Column("devices")
    @CassandraType(type = CassandraType.Name.MAP, typeArguments = {CassandraType.Name.TEXT, CassandraType.Name.UDT}, userTypeName = "device")
    private Map<String, DeviceUdt> devices;

    @Column("event_hour")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String eventHour;
}
