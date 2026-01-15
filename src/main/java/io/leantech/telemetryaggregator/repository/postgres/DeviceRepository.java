package io.leantech.telemetryaggregator.repository.postgres;

import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeviceRepository extends CrudRepository<AggregatedDeviceStats, UUID> {
}
