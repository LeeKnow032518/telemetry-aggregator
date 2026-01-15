package io.leantech.telemetryaggregator.repository.postgres;

import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface RouterRepository extends CrudRepository<AggregatedRouterStats, UUID> {
}
