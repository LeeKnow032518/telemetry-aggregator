package io.leantech.telemetryaggregator.config;

import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import io.leantech.telemetryaggregator.config.converter.DiscoveryStatusToUdtValue;
import io.leantech.telemetryaggregator.config.converter.FingerprintToUdtValue;
import io.leantech.telemetryaggregator.config.converter.UdtValueToDiscoveryStatus;
import io.leantech.telemetryaggregator.config.converter.UdtValueToFingerprint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SessionBuilderConfigurer;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableCassandraRepositories(basePackages = "io.leantech.telemetryaggregator.repository.cassandra")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.cassandra.keyspace-name}")
    private String keyspace;
    @Value("${spring.cassandra.contact-points}")
    private String contactPoints;
    @Value("${spring.cassandra.port}")
    private int port;

    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    public String getContactPoints(){
        return contactPoints;
    }

    @Override
    public int getPort(){
        return port;
    }

    @Override
    protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
        return new SessionBuilderConfigurer() {
            @Override
            public CqlSessionBuilder configure(CqlSessionBuilder cqlSessionBuilder) {
                return cqlSessionBuilder
                        .withConfigLoader(DriverConfigLoader.programmaticBuilder().withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofMillis(15000)).build());
            }
        };
    }

    @Override
    public CassandraCustomConversions customConversions(){
        return new CassandraCustomConversions(Arrays.asList(
                new UdtValueToDiscoveryStatus(),
                new UdtValueToFingerprint(),
                new FingerprintToUdtValue(this.getRequiredSession()),
                new DiscoveryStatusToUdtValue(this.getRequiredSession())
        ));
    }
}
