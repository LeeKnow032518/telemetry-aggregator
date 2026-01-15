package io.leantech.telemetryaggregator.model.cassandra.udt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@UserDefinedType
public class FingerprintUdt {

    @Column("brand")
    private String brand;

    @Column("hostname")
    private String hostname;

    @Column("model")
    private String model;

    @Column("os_name")
    private String osName;

    @Column("mac")
    private String mac;

    @Column("type")
    private String type;
}
