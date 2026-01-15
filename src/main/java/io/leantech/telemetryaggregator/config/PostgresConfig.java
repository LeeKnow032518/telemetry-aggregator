package io.leantech.telemetryaggregator.config;

import io.leantech.telemetryaggregator.config.converter.MapToJsonbConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableJdbcRepositories(basePackages = "io.leantech.telemetryaggregator.repository.postgres")
public class PostgresConfig extends AbstractJdbcConfiguration {

    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(List.of(new MapToJsonbConverter()));
    }

    @Override
    public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
        return super.jdbcDialect(operations);
    }
}
