package io.leantech.telemetryaggregator.repository.postgres;

import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ServiceRepository extends CrudRepository<AggregatedServiceStats, UUID> {
}