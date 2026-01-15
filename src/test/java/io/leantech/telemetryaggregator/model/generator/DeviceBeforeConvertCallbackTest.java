package io.leantech.telemetryaggregator.model.generator;

import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import io.leantech.telemetryaggregator.model.postgres.generator.DeviceBeforeConvertCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DeviceBeforeConvertCallbackTest {

    private DeviceBeforeConvertCallback callback;

    @BeforeEach
    void setUp() {
        callback = new DeviceBeforeConvertCallback();
    }

    @Test
    @DisplayName("Test generating UUID for aggregated device stats")
    void givenAggregatedDeviceStatsObject_whenIdIsNull_thenGenerateNewUuid() {
        //given
        AggregatedDeviceStats stats = new AggregatedDeviceStats();

        //when
        AggregatedDeviceStats result = callback.onBeforeConvert(stats);

        //then
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test onBeforeCallback when aggregated device stats UUID is present")
    void givenAggregatedDeviceStatsObject_whenIdIsPresent_thenDoNotChangeId() {
        // Given
        UUID existingId = UUID.randomUUID();
        AggregatedDeviceStats aggregatedDeviceStats = new AggregatedDeviceStats();
        aggregatedDeviceStats.setId(existingId);

        // When
        AggregatedDeviceStats aggregatedDeviceStatsIdChangeAttempt = callback.onBeforeConvert(aggregatedDeviceStats);

        // Then
        assertThat(aggregatedDeviceStatsIdChangeAttempt.getId()).isEqualTo(aggregatedDeviceStats.getId());
    }
}
