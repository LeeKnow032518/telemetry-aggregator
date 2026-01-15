package io.leantech.telemetryaggregator.service.cassandra;

import io.leantech.telemetryaggregator.model.cassandra.TelemetryEntity;
import io.leantech.telemetryaggregator.repository.cassandra.CassandraReadingRepository;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class CassandraService {

    private final CassandraReadingRepository cassandraRepository;
    private DistributionSummary rowsReadCounter;

    public CassandraService(CassandraReadingRepository readingRepository, MeterRegistry meterRegistry){
        this.cassandraRepository = readingRepository;
        this.rowsReadCounter = DistributionSummary.builder("cassandra.rows.per.query")
                .description("Number of rows read per one query")
                .baseUnit("rows")
                .tag("component", "cassandra-service")
                .tag("table", "telemetry_event")
                .register(meterRegistry);

        log.info("Metrics cassandra.rows.per.query registered");
    }


    public long readNumberOfRecords(String hour, Instant boundLower, Instant boundUpper){
        long numberOfRecords = cassandraRepository.countBoundedRecords(hour, boundLower, boundUpper);
        rowsReadCounter.record(numberOfRecords);

        return numberOfRecords;
    }

    public Slice<TelemetryEntity> readFromCassandra(String eventHour, Instant boundLower, Instant boundUpper, Pageable pageable){
        Slice<TelemetryEntity> records = cassandraRepository.findRecentRecords(eventHour, boundLower, boundUpper, pageable);
        return records;
    }
}
