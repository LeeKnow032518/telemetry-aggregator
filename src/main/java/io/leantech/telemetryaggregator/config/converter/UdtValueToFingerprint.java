package io.leantech.telemetryaggregator.config.converter;

import com.datastax.oss.driver.api.core.data.UdtValue;
import org.springframework.core.convert.converter.Converter;
import io.leantech.telemetryaggregator.model.cassandra.udt.FingerprintUdt;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UdtValueToFingerprint implements Converter<UdtValue, FingerprintUdt> {

    @Override
    public FingerprintUdt convert(UdtValue source) {
        return FingerprintUdt.builder()
                .brand(source.getString("brand"))
                .hostname(source.getString("hostname"))
                .model(source.getString("model"))
                .osName(source.getString("os_name"))
                .mac(source.getString("mac"))
                .type(source.getString("type"))
                .build();

    }
}
