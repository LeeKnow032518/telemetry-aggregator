package io.leantech.telemetryaggregator.repository.postgres;

import io.leantech.telemetryaggregator.it.AbstractTelemetryAggregatorBaseTest;
import io.leantech.telemetryaggregator.model.postgres.AggregatedRouterStats;
import io.leantech.telemetryaggregator.model.postgres.generator.RouterBeforeConvertCallback;
import io.leantech.telemetryaggregator.utils.postgres.DataUtilsRouter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RouterBeforeConvertCallback.class)
public class RouterRepositoryTests extends AbstractTelemetryAggregatorBaseTest {

    @Autowired
    private RouterRepository routerRepository;

    @BeforeEach
    public void setUp(){
        routerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save aggregated router stats functionality")
    public void givenAggregatedRouterStatsObject_whenSave_thenAggregatedRouterStatsIsCreated(){
        //given
        AggregatedRouterStats aggregatedRouterStats = DataUtilsRouter.getRouterHermanTransient();

        //when
        AggregatedRouterStats savedAggregatedRouterStats = routerRepository.save(aggregatedRouterStats);

        //then
        assertThat(savedAggregatedRouterStats).isNotNull();
        assertThat(savedAggregatedRouterStats.getId()).isNotNull();
    }
}
