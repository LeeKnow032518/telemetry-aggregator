package io.leantech.telemetryaggregator.model.generator;

import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;
import io.leantech.telemetryaggregator.model.postgres.generator.ServiceBeforeConvertCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ServiceBeforeConvertCallbackTests {

    private ServiceBeforeConvertCallback callback;

    @BeforeEach
    public void setUp(){
        callback = new ServiceBeforeConvertCallback();
    }

    @Test
    @DisplayName("Test generating UUID for aggregated service stats")
    public void givenAggregatedServiceStatsObject_whenIdIsNull_thenGenerateNewUuid(){
        //given
        AggregatedServiceStats aggregatedServiceStats = new AggregatedServiceStats();
        aggregatedServiceStats.setId(null);

        //when
        AggregatedServiceStats aggregatedServiceStatsWithId = callback.onBeforeConvert(aggregatedServiceStats);

        //then
        assertThat(aggregatedServiceStatsWithId.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test onBeforeCallback when aggregated service stats UUID is present")
    void givenAggregatedServiceStatsObject_whenIdIsPresent_thenDoNotChangeId() {
        // Given
        UUID existingId = UUID.randomUUID();
        AggregatedServiceStats aggregatedServiceStats = new AggregatedServiceStats();
        aggregatedServiceStats.setId(existingId);

        // When
        AggregatedServiceStats aggregatedServiceStatsIdChangeAttempt = callback.onBeforeConvert(aggregatedServiceStats);

        // Then
        assertThat(aggregatedServiceStatsIdChangeAttempt.getId()).isEqualTo(aggregatedServiceStats.getId());
    }

}
