package io.leantech.telemetryaggregator.model.postgres;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("aggregated_device_stats")
public class AggregatedDeviceStats extends AggregateResult{

    @Id
    @PrimaryKey
    private UUID id;

    @Column("timestamp")
    private Instant timestamp;

    @Column("ip")
    @NonNull
    private String ip;

    @Column("brand")
    private String brand;

    @Column("model")
    private String model;

    @Column("os_name")
    private String osName;

    @Column("mac")
    private String mac;

    @Column("cpu_usage_avg")
    private double cpuUsageAvg;

    @Column("memory_usage_avg")
    private double memoryUsageAvg;

    @Column("services_count")
    private int servicesCount;

    public AggregatedDeviceStats(Instant timestamp, @NonNull String ip, String brand, String model, String osName, String mac, double cpuUsageAvg, double memoryUsageAvg, int servicesCount) {
        this.timestamp = timestamp;
        this.ip = ip;
        this.brand = brand;
        this.model = model;
        this.osName = osName;
        this.mac = mac;
        this.cpuUsageAvg = cpuUsageAvg;
        this.memoryUsageAvg = memoryUsageAvg;
        this.servicesCount = servicesCount;
    }
}
