package io.leantech.telemetryaggregator.repository.postgres;

import io.leantech.telemetryaggregator.config.PostgresConfig;
import io.leantech.telemetryaggregator.it.AbstractTelemetryAggregatorBaseTest;
import io.leantech.telemetryaggregator.model.postgres.AggregatedDeviceStats;
import io.leantech.telemetryaggregator.model.postgres.generator.DeviceBeforeConvertCallback;
import io.leantech.telemetryaggregator.utils.postgres.DataUtilsDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DeviceBeforeConvertCallback.class, PostgresConfig.class})
public class DeviceRepositoryTests extends AbstractTelemetryAggregatorBaseTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeEach
    public void setUp(){

        deviceRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save aggregated device stats functionality")
    public void givenAggregatedDeviceStatsObject_whenSave_thenAggregatedDeviceStatsIsCreated(){
        //given
        AggregatedDeviceStats aggregatedDeviceStats = DataUtilsDevice.getDevicePlumTransient();

        //when
        AggregatedDeviceStats savedAggregatedDeviceStats = deviceRepository.save(aggregatedDeviceStats);
        //then
        assertThat(savedAggregatedDeviceStats).isNotNull();
        assertThat(savedAggregatedDeviceStats.getId()).isNotNull();
    }
}
