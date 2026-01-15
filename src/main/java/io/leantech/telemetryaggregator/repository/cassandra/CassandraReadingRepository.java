package io.leantech.telemetryaggregator.repository.cassandra;

import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface CassandraReadingRepository extends CassandraRepository<TelemetryEntity, UUID> {

    // time bounded query
    @Query("SELECT * FROM telemetry_event WHERE event_hour = :hour AND event_timestamp >= :start AND event_timestamp <= :end")
    Slice<TelemetryEntity> findRecentRecords(@Param("hour") String hour,
                                             @Param("start") Instant start,
                                             @Param("end") Instant end,
                                             Pageable pageable);

    // count how many records suit for aggregation
    @Query("SELECT COUNT(*) FROM telemetry_event WHERE event_hour = :hour AND event_timestamp >= :start AND event_timestamp <= :end")
    long countBoundedRecords(@Param("hour") String hour,
                             @Param("start") Instant start,
                             @Param("end") Instant end);
}
