package io.leantech.telemetryaggregator.model.cassandra.udt;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode(of = {"ip", "fingerprint"})
@Data
@Builder(toBuilder = true)
@UserDefinedType("device")
public class DeviceUdt {

    @Column("ip")
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    private List<String> ip;

    @Column("services")
    @CassandraType(type = CassandraType.Name.MAP, typeArguments = {CassandraType.Name.TEXT, CassandraType.Name.UDT}, userTypeName = "service")
    private Map<String, ServiceUdt> services;

    @Column("fingerprint")
    @CassandraType(type = CassandraType.Name.UDT, userTypeName = "fingerprint")
    private FingerprintUdt fingerprint;

    @Column("cpu_usage")
    private int cpuUsage;

    @Column("memory_usage")
    private int memoryUsage;

}
