package io.leantech.telemetryaggregator.model.generator;

import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import io.leantech.telemetryaggregator.model.postgres.generator.RouterBeforeConvertCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RouterBeforeConvertCallbackTests {

    private RouterBeforeConvertCallback callback;

    @BeforeEach
    public void setUp(){
        callback = new RouterBeforeConvertCallback();
    }

    @Test
    @DisplayName("Test generating UUID for aggregated router stats")
    public void givenAggregatedRouterStatsObject_whenIdIsNull_thenGenerateNewUuid(){
        //given
        AggregatedRouterStats aggregatedRouterStats = new AggregatedRouterStats();
        aggregatedRouterStats.setId(null);

        //when
        AggregatedRouterStats aggregatedRouterStatsWithId = callback.onBeforeConvert(aggregatedRouterStats);

        //then
        assertThat(aggregatedRouterStatsWithId.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test onBeforeCallback when aggregated router stats UUID is present")
    void givenAggregatedRouterStatsObject_whenIdIsPresent_thenDoNotChangeId() {
        // Given
        UUID existingId = UUID.randomUUID();
        AggregatedRouterStats aggregatedRouterStats = new AggregatedRouterStats();
        aggregatedRouterStats.setId(existingId);

        // When
        AggregatedRouterStats aggregatedRouterStatsIdChangeAttempt = callback.onBeforeConvert(aggregatedRouterStats);

        // Then
        assertThat(aggregatedRouterStatsIdChangeAttempt.getId()).isEqualTo(aggregatedRouterStats.getId());
    }
}
