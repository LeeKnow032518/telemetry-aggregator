package io.leantech.telemetryaggregator.it;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Testcontainers
public abstract class AbstractTelemetryAggregatorBaseTest {

    @Container
    static PostgreSQLContainer POSTGRE_SQL_CONTAINER= new PostgreSQLContainer<>("postgres:latest")
            .withUsername("postgres")
            .withPassword("postgres")
            .withDatabaseName("telemetry-aggregator-test");

    @Container
    static CassandraContainer CASSANDRA_CONTAINER= new CassandraContainer("cassandra:latest");

    @BeforeAll
    static void setUp(){
        try {
            String script = new String(Files.readAllBytes(Paths.get("src/test/resources/create_table.cql")));

            executeCqlScript(CASSANDRA_CONTAINER, script);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry resource){
        resource.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        resource.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        resource.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);

        resource.add("spring.cassandra.keyspace-name", () -> "telemetry");
        resource.add("spring.cassandra.contact-points", CASSANDRA_CONTAINER::getHost);
        resource.add("spring.cassandra.local-datacenter", CASSANDRA_CONTAINER::getLocalDatacenter);
        resource.add("spring.cassandra.port",  () -> CASSANDRA_CONTAINER.getContactPoint().getPort());
    }

    public static void executeCqlScript(CassandraContainer container, String cql) {
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(container.getContactPoint())
                .withLocalDatacenter(container.getLocalDatacenter())
                .build()) {
            for (String statement : cql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    session.execute(statement);
                }
            }
        }
    }

}
