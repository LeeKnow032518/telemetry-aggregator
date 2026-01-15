package io.leantech.telemetryaggregator.repository.postgres;

import io.leantech.telemetryaggregator.it.AbstractTelemetryAggregatorBaseTest;
import io.leantech.telemetryaggregator.model.postgres.AggregatedServiceStats;
import io.leantech.telemetryaggregator.model.postgres.generator.ServiceBeforeConvertCallback;
import io.leantech.telemetryaggregator.utils.postgres.DataUtilsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ServiceBeforeConvertCallback.class)
public class ServiceRepositoryTests extends AbstractTelemetryAggregatorBaseTest {

    @Autowired
    private ServiceRepository serviceRepository;

    @BeforeEach
    public void setUp(){
        serviceRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save aggregated service stats functionality")
    public void givenAggregatedServiceStatsObject_whenSave_thenAggregatedServiceStatsIsCreated(){
        //given
        AggregatedServiceStats aggregatedServiceStats = DataUtilsService.getMusicServiceTransient();

        aggregatedServiceStats.setQualityCountsString(aggregatedServiceStats.getQualityCounts().toString());
        //when
        AggregatedServiceStats savedAggregatedServiceStats = serviceRepository.save(aggregatedServiceStats);

        //then
        assertThat(savedAggregatedServiceStats).isNotNull();
        assertThat(savedAggregatedServiceStats.getId()).isNotNull();
    }
//
//    import com.google.gson.Gson;
//import org.junit.jupiter.api.Test;
//
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//    class GsonTest {
//
//        @Test
//        void testMapToJson() {
//            Map<String, Integer> counts = Map.of("GOOD", 10, "WARNING", 5, "BAD", 7);
//            Gson gson = new Gson();
//            String json = gson.toJson(counts);
//
//            assertThat(json).contains("\"GOOD\":10");
//            assertThat(json).contains("\"WARNING\":5");
//            assertThat(json).contains("\"BAD\":7");
//        }
//    }
}
